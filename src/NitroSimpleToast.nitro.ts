import type { HybridObject } from 'react-native-nitro-modules';

export type ToastPreset = 'done' | 'error' | 'none';

export type ToastHaptic = 'success' | 'warning' | 'error' | 'none';

export type ToastFrom = 'top' | 'bottom';

export type ToastTheme = 'light' | 'dark' | 'system';

export interface ToastOptions {
  title: string;
  message?: string;
  /**
   * Defaults to `none`.
   */
  preset?: ToastPreset;
  /**
   * Duration in seconds.
   */
  duration?: number;
  haptic?: ToastHaptic;
  /**
   * Defaults to `true`.
   */
  shouldDismissByDrag?: boolean;
  /**
   * Change the presentation side.
   * @platform ios
   */
  from?: ToastFrom;
  /**
   * Defaults to `system`.
   */
  theme?: ToastTheme;
}

export interface NitroSimpleToast
  extends HybridObject<{ ios: 'swift'; android: 'kotlin' }> {
  show(options: ToastOptions): void;
}
