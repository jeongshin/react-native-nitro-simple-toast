import { NitroModules } from 'react-native-nitro-modules';
import type { NitroSimpleToast, ToastOptions } from './NitroSimpleToast.nitro';

const NitroSimpleToastHybridObject =
  NitroModules.createHybridObject<NitroSimpleToast>('NitroSimpleToast');

export type {
  ToastOptions,
  ToastPreset,
  ToastHaptic,
  ToastFrom,
  ToastTheme,
} from './NitroSimpleToast.nitro';

export function toast(options: ToastOptions): void {
  NitroSimpleToastHybridObject.show(options);
}
