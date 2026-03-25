import { NitroModules } from 'react-native-nitro-modules';
import type { NitroSimpleToast } from './NitroSimpleToast.nitro';

const NitroSimpleToastHybridObject =
  NitroModules.createHybridObject<NitroSimpleToast>('NitroSimpleToast');

export function multiply(a: number, b: number): number {
  return NitroSimpleToastHybridObject.multiply(a, b);
}
