const API_BASE_URL = process.env.API_BASE_URL || "http://localhost:8003/api/v1";

function sleep(ms) {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

async function getSession(page) {
  return page.evaluate(() => ({
    token: localStorage.getItem("token"),
    userId: localStorage.getItem("userId"),
    username: localStorage.getItem("username"),
  }));
}

function authHeaders(session) {
  return {
    Accept: "*/*",
    Authorization: `Bearer ${session.token}`,
    "Content-Type": "application/json",
  };
}

async function requestJson(response) {
  const raw = await response.text();
  if (!raw) {
    return {};
  }

  try {
    return JSON.parse(raw);
  } catch (error) {
    return { raw };
  }
}

async function waitForLoginReady(request, username, password, timeoutMs = 180_000) {
  const startedAt = Date.now();
  let lastBody = "";

  while (Date.now() - startedAt < timeoutMs) {
    const response = await request.post(`${API_BASE_URL}/auth/login`, {
      data: {
        userName: username,
        password,
      },
    });

    if (response.ok()) {
      return;
    }

    lastBody = await response.text().catch(() => "");
    await sleep(3_000);
  }

  throw new Error(
    `Login never became ready for ${username}. Last response: ${lastBody.slice(0, 500)}`
  );
}

async function getTask(page, request, taskId) {
  const session = await getSession(page);
  const response = await request.get(`${API_BASE_URL}/tasks/${taskId}`, {
    headers: authHeaders(session),
  });

  if (!response.ok()) {
    throw new Error(`Failed to get task ${taskId}: ${response.status()}`);
  }

  return requestJson(response);
}

async function waitForTaskStatus(page, request, taskId, expectedStatus, timeoutMs = 90_000) {
  const startedAt = Date.now();
  let latestStatus = "";

  while (Date.now() - startedAt < timeoutMs) {
    const task = await getTask(page, request, taskId);
    latestStatus = task.status;
    if (latestStatus === expectedStatus) {
      return task;
    }

    await sleep(2_000);
  }

  throw new Error(
    `Task ${taskId} never reached status ${expectedStatus}. Latest status: ${latestStatus}`
  );
}

async function changeTaskStatus(page, request, taskId, status, message = "") {
  const session = await getSession(page);
  const response = await request.put(`${API_BASE_URL}/tasks/status/${taskId}`, {
    headers: authHeaders(session),
    data: {
      status,
      type: "",
      message,
      memberId: session.userId,
    },
  });

  if (!response.ok()) {
    const body = await response.text().catch(() => "");
    throw new Error(
      `Failed to change task ${taskId} to ${status}. HTTP ${response.status()}: ${body}`
    );
  }

  return requestJson(response);
}

async function updateWorkStatus(page, request, taskId, startTime, endTime) {
  const session = await getSession(page);
  const response = await request.put(`${API_BASE_URL}/tasks/work-status`, {
    headers: authHeaders(session),
    data: {
      taskId,
      startTime,
      endTime,
    },
  });

  if (!response.ok()) {
    const body = await response.text().catch(() => "");
    throw new Error(
      `Failed to update work status for ${taskId}. HTTP ${response.status()}: ${body}`
    );
  }

  return requestJson(response);
}

async function updateBugStatuses(page, request, taskId, updates) {
  const session = await getSession(page);
  const response = await request.put(`${API_BASE_URL}/bugs/${taskId}`, {
    headers: authHeaders(session),
    data: updates,
  });

  if (!response.ok()) {
    const body = await response.text().catch(() => "");
    throw new Error(
      `Failed to update bug status for ${taskId}. HTTP ${response.status()}: ${body}`
    );
  }

  return requestJson(response);
}

module.exports = {
  API_BASE_URL,
  changeTaskStatus,
  getTask,
  getSession,
  updateBugStatuses,
  updateWorkStatus,
  waitForLoginReady,
  waitForTaskStatus,
};
