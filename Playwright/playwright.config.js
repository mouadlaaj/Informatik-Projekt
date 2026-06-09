const { defineConfig } = require("@playwright/test");

const slowMo = Number(process.env.PLAYWRIGHT_SLOW_MO || 1000);

module.exports = defineConfig({
  testDir: "./tests",
  fullyParallel: false,
  workers: 1,
  timeout: 20 * 60 * 1000,
  expect: {
    timeout: 30_000,
  },
  reporter: [["list"], ["html", { open: "never" }]],
  use: {
    baseURL: process.env.APP_BASE_URL || "http://localhost:3000",
    headless: false,
    launchOptions: {
      slowMo,
    },
    viewport: {
      width: 1600,
      height: 1000,
    },
    locale: "en-US",
    timezoneId: "Europe/Berlin",
    trace: "retain-on-failure",
    screenshot: "only-on-failure",
    video: "retain-on-failure",
  },
});
