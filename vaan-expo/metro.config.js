const { getDefaultConfig } = require('expo/metro-config');

/** @type {import('expo/metro-config').MetroConfig} */
const config = getDefaultConfig(__dirname);

// Enable tsconfig `paths` (the "@/..." alias) resolution.
config.resolver.unstable_enablePackageExports = true;

module.exports = config;
