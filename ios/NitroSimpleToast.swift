import SPIndicator
import UIKit
import NitroModules

class NitroSimpleToast: HybridNitroSimpleToastSpec {
    func show(options: ToastOptions) throws -> Void {
        DispatchQueue.main.async {
            let indicatorView: SPIndicatorView

            switch options.preset ?? .done {
            case .done:
                indicatorView = SPIndicatorView(
                    title: options.title,
                    message: options.message,
                    preset: .done
                )
            case .error:
                indicatorView = SPIndicatorView(
                    title: options.title,
                    message: options.message,
                    preset: .error
                )
            case .none:
                indicatorView = SPIndicatorView(
                    title: options.title,
                    message: options.message
                )
            }

            if let duration = options.duration {
                indicatorView.duration = duration
            }

            if let shouldDismissByDrag = options.shouldDismissByDrag {
                indicatorView.dismissByDrag = shouldDismissByDrag
            }

            switch options.from ?? .top {
            case .top:
                indicatorView.presentSide = .top
            case .bottom:
                indicatorView.presentSide = .bottom
            }

            let haptic: SPIndicatorHaptic
            switch options.haptic ?? .success {
            case .success:
                haptic = .success
            case .warning:
                haptic = .warning
            case .error:
                haptic = .error
            case .none:
                haptic = .none
            }

            indicatorView.present(haptic: haptic)
        }
    }
}
