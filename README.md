# react-native-nitro-simple-toast

A native toast library for React Native powered by [Nitro Modules](https://nitro.margelo.com/).

Inspired by [burnt](https://github.com/nandorojo/burnt), but without the Expo dependency.

- **iOS**: Native floating indicator via [SPIndicator](https://github.com/ivanvorobei/SPIndicator) — displays above modals and all other views
- **Android**: Native `android.widget.Toast` with haptic feedback

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
toast({ title: 'Success!' });

// Full options
toast({
  title: 'Complete',
  message: 'File uploaded successfully',
  preset: 'done',
  duration: 2,
  haptic: 'success',
  shouldDismissByDrag: true,
  from: 'top',
});
```

## API

### `toast(options: ToastOptions): void`

| Option | Type | Default | Description |
|---|---|---|---|
| `title` | `string` | **required** | Toast title text |
| `message` | `string` | — | Optional subtitle/message |
| `preset` | `'done' \| 'error' \| 'none'` | `'done'` | Visual preset (iOS animated icon) |
| `duration` | `number` | — | Display duration in seconds |
| `haptic` | `'success' \| 'warning' \| 'error' \| 'none'` | `'success'` | Haptic feedback type |
| `shouldDismissByDrag` | `boolean` | `true` | Allow drag to dismiss (iOS only) |
| `from` | `'top' \| 'bottom'` | `'top'` | Presentation side (iOS only) |

## Platform Notes

### iOS

Full support for all options. Uses SPIndicator for native floating indicators with animated preset icons (checkmark, error cross).

### Android

Uses `android.widget.Toast` with haptic vibration feedback. The following options are iOS-only and ignored on Android:

- `from` — Android toast always appears at the bottom
- `shouldDismissByDrag` — Android toast is not draggable
- `preset` — No visual icon on Android toast
- `duration` — Maps to `Toast.LENGTH_SHORT` (<= 2s) or `Toast.LENGTH_LONG` (> 2s)

## Contributing

- [Development workflow](CONTRIBUTING.md#development-workflow)
- [Sending a pull request](CONTRIBUTING.md#sending-a-pull-request)
- [Code of conduct](CODE_OF_CONDUCT.md)

## License

MIT
