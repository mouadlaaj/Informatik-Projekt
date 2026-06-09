const { expect } = require("@playwright/test");

const APP_BASE_URL = process.env.APP_BASE_URL || "http://localhost:3000";
const STEP_PAUSE_MS = Number(process.env.STEP_PAUSE_MS || 1000);

function pad(value) {
  return String(value).padStart(2, "0");
}

function formatDate(date) {
  return `${pad(date.getMonth() + 1)}/${pad(date.getDate())}/${date.getFullYear()}`;
}

function formatDateTime(date) {
  const hours24 = date.getHours();
  const hours12 = hours24 % 12 || 12;
  const meridiem = hours24 >= 12 ? "PM" : "AM";
  return `${formatDate(date)} ${pad(hours12)}:${pad(date.getMinutes())} ${meridiem}`;
}

async function pause(page, ms = STEP_PAUSE_MS) {
  await page.waitForTimeout(ms);
}

async function gotoApp(page, path = "/") {
  await page.goto(new URL(path, APP_BASE_URL).toString(), {
    waitUntil: "domcontentloaded",
  });
}

async function clearSession(page) {
  await gotoApp(page, "/");
  await page.evaluate(() => {
    localStorage.clear();
    sessionStorage.clear();
  });
  await page.context().clearCookies();
  await gotoApp(page, "/");
}

async function login(page, username, password) {
  await clearSession(page);
  await expect(page.getByLabel("Username")).toBeVisible();
  await page.getByLabel("Username").fill(username);
  await pause(page, 200);
  await page.getByLabel("Password").fill(password);
  await pause(page, 200);
  await page.getByRole("button", { name: "Sign In" }).click();
  await expect(page.getByText("TASK BOARD", { exact: true })).toBeVisible({
    timeout: 60_000,
  });
  await page.evaluate(() => {
    const userId = localStorage.getItem("userId");
    if (userId) {
      localStorage.setItem("username", userId);
    }
  });
  await pause(page);
}

async function firstVisible(locator) {
  const total = await locator.count();
  for (let index = 0; index < total; index += 1) {
    const candidate = locator.nth(index);
    if (await candidate.isVisible().catch(() => false)) {
      return candidate;
    }
  }
  return null;
}

async function findVisibleLabeledControl(container, label) {
  const fallback = container
    .locator("label")
    .filter({ hasText: label })
    .locator(
      "xpath=following::*[(self::input or self::textarea or @role='combobox' or @role='button')][1]"
    );

  const candidates = [
    container.getByRole("combobox", { name: label }),
    container.getByRole("textbox", { name: label }),
    container.getByLabel(label),
    fallback,
  ];

  for (const candidate of candidates) {
    const visible = await firstVisible(candidate);
    if (visible) {
      return visible;
    }
  }

  throw new Error(`No visible control found for label "${label}".`);
}

async function fillField(page, label, value, container = page) {
  const control = await findVisibleLabeledControl(container, label);
  await control.click();
  await control.press("Control+A");
  await control.fill(value);
  await pause(page, 200);
}

async function fillDateField(page, label, value, container = page) {
  await fillField(page, label, value, container);
  await page.keyboard.press("Tab");
  await pause(page, 300);
}

async function selectMenuOption(page, label, optionText, container = page) {
  const control = await findVisibleLabeledControl(container, label);
  await control.click();
  const option = page.getByRole("option", { name: optionText, exact: true });
  await expect(option).toBeVisible();
  await option.click();
  await pause(page);
}

async function selectFirstMenuOption(page, label, container = page) {
  const control = await findVisibleLabeledControl(container, label);
  await control.click();
  const option = page.getByRole("option").first();
  await expect(option).toBeVisible();
  await option.click();
  await pause(page);
}

async function selectAutocompleteOption(page, label, optionText, container = page) {
  const control = await findVisibleLabeledControl(container, label);
  await control.click();
  await control.press("Control+A");
  await control.fill(optionText);
  const option = page.getByRole("option", { name: optionText, exact: true });
  await expect(option).toBeVisible();
  await option.click();
  await pause(page);
}

async function addTagValues(page, placeholder, values, container = page) {
  const input = container.getByPlaceholder(placeholder);
  await expect(input).toBeVisible();
  for (const value of values) {
    await input.fill(value);
    await pause(page, 200);
    await input.press("Enter");
    await pause(page, 200);
  }
}

async function clickVisibleManageSpeedDial(page) {
  const dial = page
    .locator('button[aria-label="SpeedDial controlled open example"]:visible')
    .first();
  await expect(dial).toBeVisible();
  await dial.click();
  await pause(page);
}

async function clickAddTaskSpeedDial(page) {
  const dial = page.locator('button[aria-label="Add Task"]:visible').first();
  await expect(dial).toBeVisible();
  await dial.click();
  await pause(page);
}

async function waitForToast(page, message) {
  const toast = page.locator(".Toastify__toast").filter({ hasText: message }).last();
  await expect(toast).toBeVisible({ timeout: 30_000 });
  await pause(page);
}

async function openNotifications(page) {
  const bellIcon = page.locator("header svg").first();
  await expect(bellIcon).toBeVisible();
  await bellIcon.click();
  await expect(page.getByText("Notifications", { exact: true })).toBeVisible();
  await pause(page);
}

async function expectNotification(page, text) {
  await openNotifications(page);
  await expect(page.getByText(text, { exact: false }).first()).toBeVisible({
    timeout: 30_000,
  });
  await page.keyboard.press("Escape");
  await pause(page);
}

async function filterTaskBoard(page, query) {
  await page.getByRole("button", { name: "FILTERS" }).click();
  await fillField(page, "Title or Task Id", query);
  await pause(page, 800);
  await page.keyboard.press("Escape");
  await pause(page, 300);
}

async function openTaskPopup(page, taskId, taskTitle) {
  await filterTaskBoard(page, taskId);
  const card = page.locator(`[data-testid="${taskId}"]`).first();
  await expect(card).toBeVisible({ timeout: 60_000 });
  await card.getByText(taskTitle, { exact: false }).click();
  const dialog = page.getByRole("dialog").last();
  await expect(dialog).toBeVisible();
  await pause(page);
  return dialog;
}

async function closeDialog(page) {
  await page.keyboard.press("Escape");
  await pause(page, 400);
}

async function fillRichTextEditor(page, container, text, position = "first") {
  const editors = container.locator(".rdw-editor-main [contenteditable='true']");
  const editor =
    position === "last" ? editors.last() : editors.first();

  await expect(editor).toBeVisible();
  await editor.click();
  await pause(page, 200);
  await page.keyboard.press("Control+A");
  await page.keyboard.type(text, { delay: 40 });
  await pause(page, 300);
}

module.exports = {
  APP_BASE_URL,
  STEP_PAUSE_MS,
  addTagValues,
  clickAddTaskSpeedDial,
  clickVisibleManageSpeedDial,
  closeDialog,
  expectNotification,
  fillDateField,
  fillField,
  fillRichTextEditor,
  filterTaskBoard,
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
};
