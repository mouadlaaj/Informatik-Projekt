const MANAGER = {
  username: process.env.MANAGER_USERNAME || "tsk_001",
  password: process.env.MANAGER_PASSWORD || "Task@1234",
};

function createSuffix() {
  const timestamp = Date.now().toString(36).toUpperCase();
  const random = Math.random().toString(36).slice(2, 6).toUpperCase();
  return `${timestamp}${random}`.replace(/[^A-Z0-9]/g, "").slice(-8);
}

function buildScenarioData() {
  const suffix = createSuffix();
  const emailSuffix = suffix.toLowerCase();
  const numericTail = Number.parseInt(suffix.replace(/[A-Z]/g, "7"), 10)
    .toString()
    .slice(-6)
    .padStart(6, "0");

  const makePhone = (prefix) => `${prefix}${numericTail}`.slice(0, 10);

  const members = {
    designer: {
      firstName: "Auto",
      lastName: `Des${suffix}`,
      memberId: `des${emailSuffix.slice(0, 6)}`,
      email: `designer.${emailSuffix}@example.com`,
      mobile: makePhone("7011"),
      designation: "DESIGNER",
      role: "EMPLOYEE",
      gender: "male",
      address: `Dynamic designer address ${suffix}`,
      password: "Task@1234",
    },
    developer: {
      firstName: "Auto",
      lastName: `Dev${suffix}`,
      memberId: `dev${emailSuffix.slice(0, 6)}`,
      email: `developer.${emailSuffix}@example.com`,
      mobile: makePhone("7022"),
      designation: "DEVELOPER",
      role: "EMPLOYEE",
      gender: "male",
      address: `Dynamic developer address ${suffix}`,
      password: "Task@1234",
    },
    tester: {
      firstName: "Auto",
      lastName: `Tes${suffix}`,
      memberId: `tst${emailSuffix.slice(0, 6)}`,
      email: `tester.${emailSuffix}@example.com`,
      mobile: makePhone("7033"),
      designation: "TESTER",
      role: "EMPLOYEE",
      gender: "female",
      address: `Dynamic tester address ${suffix}`,
      password: "Task@1234",
    },
  };

  return {
    suffix,
    members,
    teamName: `TEAM-${suffix}`,
    projectName: `PR${suffix}`,
    projectDescription: `Dynamic project ${suffix} for repeatable end-to-end coverage.`,
    projectTags: ["AUTO", "FLOW", `R${suffix.slice(0, 4)}`],
    taskTitle: `Workflow ${suffix}`,
    taskShortDescription: `Dynamic task ${suffix} for the doc-based workflow.`,
    taskDescription: `This task validates the full designer, developer, tester, and reporting journey for run ${suffix}.`,
    commentText: `Comment for workflow ${suffix}`,
    bugTitle: `Bug ${suffix}`,
    bugDescription: `Dynamic bug report for workflow ${suffix}`,
  };
}

module.exports = {
  MANAGER,
  buildScenarioData,
};
