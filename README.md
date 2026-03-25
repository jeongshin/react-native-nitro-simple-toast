# react-native-nitro-simple-toast

A native toast library for React Native powered by [Nitro Modules](https://nitro.margelo.com/).

Inspired by [burnt](https://github.com/nandorojo/burnt), but without the Expo dependency.

- **iOS**: Native floating indicator via [SPIndicator](https://github.com/ivanvorobei/SPIndicator) — displays above modals and all other views
- **Android**: Custom floating window via [EasyWindow](https://github.com/getActivity/EasyWindow) — reliable on all devices including Android 13+

## Installation

```sh
npm install react-native-nitro-simple-toast react-native-nitro-modules
```

> `react-native-nitro-modules` is a peer dependency required by this library.

For iOS:

```sh
cd ios && pod install
```

## Usage

```ts
import { toast } from 'react-native-nitro-simple-toast';

// Simple toast
toast({ title: 'Hello!' });

// With preset icon
toast({
  title: 'Success',
  message: 'File uploaded',
  preset: 'done',
  haptic: 'success',
});

// Themed toast
toast({
  title: 'Dark Mode',
  message: 'Forced dark theme',
  preset: 'done',
  theme: 'dark',
});
```

## API

### `toast(options: ToastOptions): void`

| Option | Type | Default | Description |
|---|---|---|---|
| `title` | `string` | **required** | Toast title text |
| `message` | `string` | — | Optional subtitle/message |
| `preset` | `'done' \| 'error' \| 'none'` | `'none'` | Visual preset icon |
| `duration` | `number` | `3` | Display duration in seconds |
| `haptic` | `'success' \| 'warning' \| 'error' \| 'none'` | `'success'` | Haptic feedback type |
| `shouldDismissByDrag` | `boolean` | `true` | Allow drag to dismiss (iOS only) |
| `from` | `'top' \| 'bottom'` | `'top'` | Presentation side |
| `theme` | `'light' \| 'dark' \| 'system'` | `'system'` | Toast color theme |

## Platform Notes

### iOS

Full support for all options. Uses SPIndicator for native floating indicators with animated preset icons (checkmark, error cross). Theme is applied via `overrideUserInterfaceStyle`.

### Android

Uses EasyWindow (`WindowManager.addView()`) for reliable toast display on all Android versions, including devices that restrict `android.widget.Toast`. Supports custom theme colors, preset icons, slide animations, and position control.

- `shouldDismissByDrag` — Not yet supported on Android

## License

MIT
