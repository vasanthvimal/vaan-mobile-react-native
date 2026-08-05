module.exports = function (api) {
  api.cache(true);
  return {
    presets: [['babel-preset-expo', { reactCompiler: true }]],
    // Path alias "@/*" is resolved by Expo's Metro config from tsconfig `paths`,
    // so no module-resolver plugin is required.
    plugins: [
      // react-native-worklets/plugin must stay last (Reanimated 4 requirement).
      'react-native-worklets/plugin',
    ],
  };
};
