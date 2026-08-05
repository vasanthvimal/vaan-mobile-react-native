// Extend Jest with React Native specific matchers.
import '@testing-library/jest-native/extend-expect';

// Silence noisy Animated/useNativeDriver warnings in the test runtime.
jest.mock('react-native/Libraries/Animated/NativeAnimatedHelper');
