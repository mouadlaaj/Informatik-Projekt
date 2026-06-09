const { test, expect } = require("@playwright/test");

const { MANAGER, buildScenarioData } = require("./helpers/data");
const {
  addTagValues,
  clickAddTaskSpeedDial,
  clickVisibleManageSpeedDial,
  closeDialog,
  expectNotification,
  fillDateField,
  fillField,
  fillRichTextEditor,
  formatDate,
  formatDateTime,
  gotoApp,
  login,
  openTaskPopup,
  pause,
  selectAutocompleteOption,
  selectFirstMenuOption,
  selectMenuOption,
  waitForToast,
} = require("./helpers/ui");
const {
  changeTaskStatus,
  getTask,
  updateBugStatuses,
  updateWorkStatus,
  waitForLoginReady,
  waitForTaskStatus,
} = require("./helpers/api");

const scenario = buildScenarioData();
const state = {
  ...scenario,
  taskId: null,
};

function plusMinutes(minutes) {
  return new Date(Date.now() + minutes * 60 * 1000);
}

function escapeRegExp(value) {
  return value.replace(/[.*+?^${}()|[\]\\]/g, "\\$&");
}

async function openNavigation(page, label) {
  await page.getByText(label, { exact: true }).click();
  await pause(page);
}

async function searchForManageEntity(page, label, searchValue, expectedVisibleText = searchValue) {
  await fillField(page, label, searchValue);
  await pause(page, 800);
  await expect(page.getByText(new RegExp(escapeRegExp(expectedVisibleText), "i"))).toBeVisible({
    timeout: 30_000,
  });
}

async function expectTaskStatusChip(dialog, status) {
  await expect(dialog.getByLabel(`Current status is (${status})`)).toBeVisible({
    timeout: 30_000,
  });
}

async function createMember(page, member) {
  await clickVisibleManageSpeedDial(page);

  const dialog = page.getByRole("dialog").last();
  await expect(dialog).toBeVisible();

  await fillField(page, "First Name", member.firstName, dialog);
  await fillField(page, "Last Name", member.lastName, dialog);
  await fillField(page, "Mobile", member.mobile, dialog);
  await fillField(page, "Email", member.email, dialog);
  await fillField(page, "Address", member.address, dialog);
  await fillDateField(page, "Date of Joining", formatDate(new Date()), dialog);
  await fillField(page, "Years", "0", dialog);
  await dialog.getByLabel(member.gender === "female" ? /Female/ : /Male/).click();
  await fillDateField(page, "DOB", "01/15/1997", dialog);

  await dialog.getByRole("button", { name: "NEXT" }).click();
  await pause(page);

  await fillField(page, "Member ID", member.memberId, dialog);

  await dialog.getByRole("button", { name: "NEXT" }).click();
  await pause(page);

  await dialog.locator(`input[type="radio"][value="${member.role}"]`).check({
    force: true,
  });
  await pause(page);
  await selectMenuOption(page, "Designation", member.designation, dialog);

  await dialog.getByRole("button", { name: "SUBMIT" }).click();
  await waitForToast(page, "Member added successfully");
}

async function createTeam(page) {
  await clickVisibleManageSpeedDial(page);
  const dialog = page.getByRole("dialog").last();
  await expect(dialog).toBeVisible();

  await fillField(page, "Team Name", state.teamName, dialog);
  await selectFirstMenuOption(page, "Team Lead", dialog);

  await selectAutocompleteOption(
    page,
    "Add Team Members",
    `${state.members.designer.firstName} ${state.members.designer.lastName} (${state.members.designer.memberId})`,
    dialog
  );
  await selectAutocompleteOption(
    page,
    "Add Team Members",
    `${state.members.developer.firstName} ${state.members.developer.lastName} (${state.members.developer.memberId})`,
    dialog
  );
  await selectAutocompleteOption(
    page,
    "Add Team Members",
    `${state.members.tester.firstName} ${state.members.tester.lastName} (${state.members.tester.memberId})`,
    dialog
  );

  await dialog.getByRole("button", { name: "SUBMIT" }).click();
  await waitForToast(page, "Team added successfully");
}

async function createProject(page) {
  await clickVisibleManageSpeedDial(page);
  const dialog = page.getByRole("dialog").last();
  await expect(dialog).toBeVisible();

  const projectNameInput = dialog.locator('input[type="text"]:visible').first();
  await expect(projectNameInput).toBeVisible();
  await projectNameInput.click();
  await projectNameInput.press("Control+A");
  await projectNameInput.fill(state.projectName);
  await pause(page, 200);

  const descriptionInput = dialog.locator("textarea:visible").first();
  await expect(descriptionInput).toBeVisible();
  await descriptionInput.click();
  await descriptionInput.press("Control+A");
  await descriptionInput.fill(state.projectDescription);
  await pause(page, 200);

  await selectAutocompleteOption(page, "Add Project Teams", state.teamName, dialog);
  await addTagValues(page, "Enter tags / keywords", state.projectTags, dialog);

  await dialog.getByRole("button", { name: "SUBMIT" }).click();
  await waitForToast(page, "Project added successfully");
}

async function createTask(page) {
  await clickAddTaskSpeedDial(page);
  const dialog = page.getByRole("dialog").last();
  await expect(dialog).toBeVisible();

  const titleInput = dialog.locator('input[type="text"]:visible').first();
  await expect(titleInput).toBeVisible();
  await titleInput.click();
  await titleInput.press("Control+A");
  await titleInput.fill(state.taskTitle);
  await pause(page, 200);

  const shortDescriptionInput = dialog.locator("textarea:visible").first();
  await expect(shortDescriptionInput).toBeVisible();
  await shortDescriptionInput.click();
  await shortDescriptionInput.press("Control+A");
  await shortDescriptionInput.fill(state.taskShortDescription);
  await pause(page, 200);

  await dialog.getByRole("button", { name: "NEXT" }).click();
  await pause(page);

  await fillRichTextEditor(page, dialog, state.taskDescription, "first");
  await dialog.getByRole("button", { name: "NEXT" }).click();
  await pause(page);

  await dialog.locator('input[type="radio"][value="HIGH"]').check({
    force: true,
  });
  await pause(page);
  await fillDateField(page, "Start date", formatDateTime(plusMinutes(90)), dialog);
  await fillDateField(page, "End date", formatDateTime(plusMinutes(360)), dialog);

  const slider = dialog.getByRole("slider").first();
  await slider.focus();
  for (let index = 0; index < 4; index += 1) {
    await slider.press("ArrowRight");
  }
  await pause(page);

  await dialog.getByRole("button", { name: "NEXT" }).click();
  await pause(page);

  await selectMenuOption(page, "Project", state.projectName, dialog);
  await selectAutocompleteOption(page, "Add Tags", state.projectTags[0], dialog);
  await selectAutocompleteOption(page, "Add Tags", state.projectTags[1], dialog);

  await selectAutocompleteOption(
    page,
    "Select Designer",
    `${state.members.designer.firstName} ${state.members.designer.lastName} (${state.members.designer.memberId})`,
    dialog
  );
  await fillField(page, "Designer Estimation Time (minutes)", "30", dialog);

  await selectAutocompleteOption(
    page,
    "Select Developer",
    `${state.members.developer.firstName} ${state.members.developer.lastName} (${state.members.developer.memberId})`,
    dialog
  );
  await fillField(page, "Developer Estimation Time (minutes)", "45", dialog);

  await selectAutocompleteOption(
    page,
    "Select Tester",
    `${state.members.tester.firstName} ${state.members.tester.lastName} (${state.members.tester.memberId})`,
    dialog
  );
  await fillField(page, "Tester Estimation Time (minutes)", "20", dialog);

  const creationResponse = page.waitForResponse((response) => {
    return (
      response.url().includes("/api/v1/tasks") &&
      response.request().method() === "POST" &&
      [200, 201].includes(response.status())
    );
  });

  await dialog.getByRole("button", { name: "SUBMIT" }).click();

  const response = await creationResponse;
  const body = await response.json();
  state.taskId = body.id || body.taskId;

  await waitForToast(page, "Task created successfully");
}

async function addCommentToTask(page) {
  const dialog = await openTaskPopup(page, state.taskId, state.taskTitle);
  await expectTaskStatusChip(dialog, "TODO");
  await dialog.getByRole("button", { name: "COMMENTS" }).click();
  await pause(page);
  await fillRichTextEditor(page, dialog, state.commentText, "last");
  await dialog.getByRole("button", { name: "SUBMIT" }).last().click();
  await waitForToast(page, "Comment added successfully");
  await expect(dialog.getByText(state.commentText, { exact: false })).toBeVisible();
  await closeDialog(page);
}

async function reportBugFromTask(page) {
  const dialog = await openTaskPopup(page, state.taskId, state.taskTitle);
  await expectTaskStatusChip(dialog, "TESTING");
  await dialog.getByRole("button", { name: "BUG", exact: true }).click();
  await pause(page);
  await dialog.getByRole("button", { name: "Add Bug", exact: true }).click();
  await pause(page);
  await fillField(page, "Title", state.bugTitle, dialog);
  await selectMenuOption(page, "Severity", "High", dialog);
  await fillField(page, "Description", state.bugDescription, dialog);
  await dialog.getByRole("button", { name: "Save All Bugs" }).click();
  await waitForToast(page, "Bugs reported successfully!");
  await expect(dialog.getByText(state.bugTitle, { exact: false })).toBeVisible();
  await closeDialog(page);
}

async function confirmTaskStatusOnBoard(page, request, expectedStatus) {
  await waitForTaskStatus(page, request, state.taskId, expectedStatus);
  await gotoApp(page, "/board");
  await pause(page, 1_000);
  const dialog = await openTaskPopup(page, state.taskId, state.taskTitle);
  await expectTaskStatusChip(dialog, expectedStatus);
  await closeDialog(page);
}

async function setFutureWorkWindow(page, request, startOffsetMinutes) {
  const startTime = plusMinutes(startOffsetMinutes);
  const endTime = plusMinutes(startOffsetMinutes + 20);

  await updateWorkStatus(
    page,
    request,
    state.taskId,
    formatApiDateTime(startTime),
    formatApiDateTime(endTime)
  );
}

function formatApiDateTime(date) {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  const hours = String(date.getHours()).padStart(2, "0");
  const minutes = String(date.getMinutes()).padStart(2, "0");
  const seconds = String(date.getSeconds()).padStart(2, "0");
  return `${year}-${month}-${day}T${hours}:${minutes}:${seconds}`;
}

test.describe.serial("Task Management System workflow from the DOCX", () => {
  test("manager creates members, team, project, and task", async ({ page, request }) => {
    await login(page, MANAGER.username, MANAGER.password);

    await openNavigation(page, "MANAGE");
    await page.getByRole("tab", { name: "MEMBERS" }).click();
    await pause(page);

    await createMember(page, state.members.designer);
    await searchForManageEntity(
      page,
      "Search member here..",
      state.members.designer.memberId,
      `${state.members.designer.firstName} ${state.members.designer.lastName}`
    );

    await createMember(page, state.members.developer);
    await searchForManageEntity(
      page,
      "Search member here..",
      state.members.developer.memberId,
      `${state.members.developer.firstName} ${state.members.developer.lastName}`
    );

    await createMember(page, state.members.tester);
    await searchForManageEntity(
      page,
      "Search member here..",
      state.members.tester.memberId,
      `${state.members.tester.firstName} ${state.members.tester.lastName}`
    );

    await waitForLoginReady(request, state.members.designer.memberId, state.members.designer.password);
    await waitForLoginReady(request, state.members.developer.memberId, state.members.developer.password);
    await waitForLoginReady(request, state.members.tester.memberId, state.members.tester.password);

    await page.getByRole("tab", { name: "TEAMS" }).click();
    await pause(page);
    await createTeam(page);
    await searchForManageEntity(page, "Search team here..", state.teamName);

    await page.getByRole("tab", { name: "PROJECTS" }).click();
    await pause(page);
    await createProject(page);
    await searchForManageEntity(page, "Search project here..", state.projectName);

    await openNavigation(page, "TASK BOARD");
    await createTask(page);

    expect(state.taskId).toBeTruthy();

    const dialog = await openTaskPopup(page, state.taskId, state.taskTitle);
    await expectTaskStatusChip(dialog, "TODO");
    await expect(dialog.getByText(state.taskTitle, { exact: false })).toBeVisible();
    await closeDialog(page);
  });

  test("designer sees assignment, comments, and moves the task to development", async ({
    page,
    request,
  }) => {
    await login(page, state.members.designer.memberId, state.members.designer.password);
    await expectNotification(page, `assigned this task "${state.taskId}" to you`);

    await openNavigation(page, "TASK BOARD");
    await addCommentToTask(page);

    await changeTaskStatus(page, request, state.taskId, "DESIGN");
    await confirmTaskStatusOnBoard(page, request, "DESIGN");

    await changeTaskStatus(page, request, state.taskId, "DEVELOPMENT");
    await confirmTaskStatusOnBoard(page, request, "DEVELOPMENT");
  });

  test("developer starts work and sends the task to testing", async ({ page, request }) => {
    await login(page, state.members.developer.memberId, state.members.developer.password);
    await expectNotification(page, `Task "${state.taskTitle}" moved from DESIGN to DEVELOPMENT`);

    await openNavigation(page, "TASK BOARD");
    await openTaskPopup(page, state.taskId, state.taskTitle);
    await closeDialog(page);

    await setFutureWorkWindow(page, request, 30);
    await changeTaskStatus(page, request, state.taskId, "TESTING");
    await confirmTaskStatusOnBoard(page, request, "TESTING");
  });

  test("tester reports a bug and returns the task to development", async ({ page, request }) => {
    await login(page, state.members.tester.memberId, state.members.tester.password);
    await expectNotification(page, `Task "${state.taskTitle}" moved from DEVELOPMENT to TESTING`);

    await openNavigation(page, "TASK BOARD");
    await setFutureWorkWindow(page, request, 60);
    await reportBugFromTask(page);

    await changeTaskStatus(page, request, state.taskId, "DEVELOPMENT");
    await confirmTaskStatusOnBoard(page, request, "DEVELOPMENT");
  });

  test("developer fixes the bug and returns the task to testing", async ({ page, request }) => {
    await login(page, state.members.developer.memberId, state.members.developer.password);
    await expectNotification(page, `Task "${state.taskTitle}" moved from TESTING to DEVELOPMENT`);

    await openNavigation(page, "TASK BOARD");
    await setFutureWorkWindow(page, request, 90);

    const taskBeforeFix = await getTask(page, request, state.taskId);
    const bugUpdates = (taskBeforeFix.bugs || []).map((bug) => ({
      id: bug.id,
      status: "FIXED",
    }));

    expect(bugUpdates.length).toBeGreaterThan(0);
    await updateBugStatuses(page, request, state.taskId, bugUpdates);

    const taskAfterFix = await getTask(page, request, state.taskId);
    expect((taskAfterFix.bugs || []).every((bug) => bug.status === "FIXED")).toBeTruthy();

    await changeTaskStatus(page, request, state.taskId, "TESTING");
    await confirmTaskStatusOnBoard(page, request, "TESTING");
  });

  test("tester verifies the fix and completes the task", async ({ page, request }) => {
    await login(page, state.members.tester.memberId, state.members.tester.password);
    await expectNotification(page, `Task "${state.taskTitle}" moved from DEVELOPMENT to TESTING`);

    await openNavigation(page, "TASK BOARD");
    await setFutureWorkWindow(page, request, 120);

    const taskBeforeVerify = await getTask(page, request, state.taskId);
    const bugUpdates = (taskBeforeVerify.bugs || []).map((bug) => ({
      id: bug.id,
      status: "VERIFIED",
    }));

    expect(bugUpdates.length).toBeGreaterThan(0);
    await updateBugStatuses(page, request, state.taskId, bugUpdates);

    const taskAfterVerify = await getTask(page, request, state.taskId);
    expect((taskAfterVerify.bugs || []).every((bug) => bug.status === "VERIFIED")).toBeTruthy();

    await changeTaskStatus(page, request, state.taskId, "DONE");
    await confirmTaskStatusOnBoard(page, request, "DONE");
  });

  test("manager sees the completion notification and report data", async ({ page }) => {
    await login(page, MANAGER.username, MANAGER.password);
    await expectNotification(page, `Task "${state.taskTitle}" moved from TESTING to DONE`);

    await openNavigation(page, "REPORTS");
    await selectAutocompleteOption(page, "Select Team", state.teamName);
    await pause(page, 1_500);
    await page.getByRole("button", { name: "SEARCH" }).click();
    await pause(page, 2_000);

    await expect(page.getByText(state.teamName, { exact: false }).first()).toBeVisible({
      timeout: 60_000,
    });
    await expect(page.getByText(state.taskTitle, { exact: false }).first()).toBeVisible({
      timeout: 60_000,
    });
    await expect(
      page.getByText(state.members.developer.memberId, { exact: false }).first()
    ).toBeVisible({
      timeout: 60_000,
    });
  });
});
