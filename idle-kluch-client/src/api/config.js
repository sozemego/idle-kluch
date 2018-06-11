const localEnv = "LOCAL";
const testEnv = "TEST";
const prodEnv = "PROD";

const currentEnv = localEnv;

const configurations = {
  [ localEnv ]: {
    protocol: "http",
    wsProtocol: "ws",
    base: "localhost",
    port: 8080,
    app: "",
    version: "/api/0.1",
  },
  [ testEnv ]: {
    protocol: "http",
    wsProtocol: "ws",
    base: "localhost",
    port: 8180,
    app: "",
    version: "/api/0.1",
  },
  [ prodEnv ]: {
    protocol: "http",
    wsProtocol: "ws",
    base: "localhost",
    port: 8080,
    app: "/idle-kluch-server",
    version: "/api/0.1",
  },
};

const findCurrentConfig = () => {
  const config = configurations[ currentEnv ];
  if (!config) {
    throw new Error(`Could not find a valid network configuration for current env: ${currentEnv}
     and configurations ${JSON.stringify(configurations)}`);
  }
  return config;
};

export const networkConfig = findCurrentConfig();
