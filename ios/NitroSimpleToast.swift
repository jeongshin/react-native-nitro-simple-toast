import SPIndicator
import UIKit
import NitroModules

class NitroSimpleToast: HybridNitroSimpleToastSpec {
    func show(options: ToastOptions) throws -> Void {
        DispatchQueue.main.async {
            let indicatorView: SPIndicatorView

            switch options.preset ?? .none {
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
                // Title-only toasts are the ones that can wrap to many lines
                // (e.g. long localized copy). Use a subclass that grows its
                // height to fit instead of clipping at SPIndicator's fixed 2
                // lines / 50pt. Icon presets keep the stock view.
                indicatorView = MultilineSPIndicatorView(
                    title: options.title,
                    message: options.message
                )
            }

            switch options.theme ?? .system {
            case .light:
                indicatorView.overrideUserInterfaceStyle = .light
            case .dark:
                indicatorView.overrideUserInterfaceStyle = .dark
            case .system:
                break
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

            if let onPress = options.onPress {
                let tapHandler = ToastTapHandler { [weak indicatorView] in
                    indicatorView?.dismiss()
                    onPress()
                }
                let tapGesture = UITapGestureRecognizer(
                    target: tapHandler,
                    action: #selector(ToastTapHandler.handleTap)
                )
                indicatorView.addGestureRecognizer(tapGesture)
                // Retain the handler for the lifetime of the indicator view;
                // UIGestureRecognizer holds its target weakly.
                objc_setAssociatedObject(
                    indicatorView,
                    &ToastTapHandler.associatedKey,
                    tapHandler,
                    .OBJC_ASSOCIATION_RETAIN_NONATOMIC
                )
            }

            indicatorView.present(haptic: haptic)
        }
    }
}

/// Bridges a tap on the toast to the JS `onPress` callback. Kept alive via an
/// associated object on the indicator view (gesture targets are weak).
private final class ToastTapHandler: NSObject {
    static var associatedKey: UInt8 = 0

    private let action: () -> Void

    init(action: @escaping () -> Void) {
        self.action = action
    }

    @objc func handleTap() {
        action()
    }
}

/// SPIndicator 1.6.4 hardcodes `areaHeight = 50` and, for the title-only
/// `.title` layout grid, clamps the title label to `numberOfLines = 2`
/// (see `SPIndicatorView.layoutSubviews`). Long localized toast copy — e.g.
/// the Spanish "internet connection" retry warning — therefore gets truncated.
/// Upstream 1.6.5 does not fix this.
///
/// This subclass keeps the original font, pill styling and 50pt minimum, but
/// lets the title wrap to as many lines as it needs and grows the container to
/// fit. Only the title-only path is affected; icon presets and subtitle layouts
/// fall through to the stock behavior untouched.
///
/// ## Why the extra top-edge compensation
///
/// SPIndicator derives its present/dismiss transforms in `toPresentPosition`.
/// For `.top` the *prepare* (off-screen) transform is a **fixed**
/// `translatedBy(y: -(topInset + 50))` — the `50` is the stock `areaHeight`,
/// not the view's real height, and it does **not** read `frame.height`. Once we
/// grow the view past 50pt that fixed shift no longer clears the taller pill:
/// `extraHeight = height - 50` points stay on-screen during entry and at
/// dismissal (a ~106pt toast on a 20pt top inset leaves ~36pt peeking). The
/// `.top` *visible* transform, by contrast, is `topSafeArea - 3 + offset` and
/// positions the pill's **top** edge, so it is already height-independent and
/// renders at the correct resting spot with or without this fix.
///
/// Fix (`.top` only): shift the view's base position up by `extraHeight` and add
/// the same `extraHeight` to the public `offset`.
///   • The base shift makes the fixed prepare transform clear the full grown
///     height — the prepare bottom edge lands at `-topInset` (fully hidden),
///     exactly where a stock 50pt pill lands.
///   • The `offset` bump cancels that same base shift inside the visible /
///     fromVisible transforms (both add `+ offset`), so the on-screen resting
///     position and the drag-to-dismiss behavior stay pixel-identical to stock.
/// We move the base via `center.y` rather than `frame.origin.y`: `center` stays
/// valid once a transform is applied, whereas `frame` does not.
///
/// `.bottom` needs no compensation: its prepare transform uses the window height
/// (already clears any pill) and its visible transform subtracts
/// `self.frame.height`, so both are height-aware upstream. The short 50pt path
/// nets `extraHeight == 0` and is untouched.
///
/// The compensation is idempotent: we remember how much we've applied and only
/// move by the delta, so repeated `layoutSubviews` passes — or a `presentSide`
/// flip before presentation — never accumulate or drift. Any caller-set
/// `offset` is preserved because we only add/subtract our own delta.
private final class MultilineSPIndicatorView: SPIndicatorView {

    /// Stock SPIndicator 1.6.4 pinned `areaHeight`. The `.top` prepare transform
    /// is hardcoded to this value instead of the real (grown) height.
    private static let stockAreaHeight: CGFloat = 50

    /// Vertical base shift (and matching `offset` bump) currently applied for the
    /// `.top` dynamic-height compensation. Tracked so repeated layout passes move
    /// only by the delta instead of re-adding the whole amount.
    private var appliedTopCompensation: CGFloat = 0

    override func sizeThatFits(_ size: CGSize) -> CGSize {
        // Base returns the clamped pill width and the fixed 50pt area height.
        let base = super.sizeThatFits(size)

        // Only expand the title-only layout; anything with a subtitle keeps the
        // stock two-label vertical layout to avoid overlap regressions.
        guard let titleLabel = self.titleLabel, self.subtitleLabel == nil else {
            return base
        }

        // Same wrapping width the `.title` grid uses: the container width minus
        // the layout's horizontal margins (`titlesFullWidth`).
        let horizontalInset = layout.margins.left + layout.margins.right
        let availableWidth = base.width - horizontalInset
        guard availableWidth > 0 else { return base }

        titleLabel.numberOfLines = 0
        let titleSize = titleLabel.sizeThatFits(
            CGSize(width: availableWidth, height: .greatestFiniteMagnitude)
        )

        let verticalInset = layout.margins.top + layout.margins.bottom
        let contentHeight = ceil(titleSize.height) + verticalInset

        // Never shrink below the original 50pt area height (base.height), so
        // short messages render exactly as before.
        return CGSize(width: base.width, height: max(base.height, contentHeight))
    }

    override func layoutSubviews() {
        // Base lays the title out clamped to 2 lines and centers it in the
        // (already grown) frame. Re-run only the title fit with the clamp lifted.
        super.layoutSubviews()

        guard let titleLabel = self.titleLabel, self.subtitleLabel == nil else {
            // Not the title-only path (e.g. a subtitle was added, or presentSide
            // changed): undo any compensation we applied earlier so the view is
            // never left shifted.
            applyTopCompensation(0)
            return
        }

        // Reuse the exact wrapping width base assigned via `layoutDynamicHeight`
        // (== `titlesFullWidth`); that keeps this height identical to the one
        // `sizeThatFits` predicted.
        let width = titleLabel.frame.width
        let origin = titleLabel.frame.origin
        titleLabel.numberOfLines = 0

        // Measure the wrapped height deterministically: pass the pinned width and
        // an unbounded height so the result is never clamped by the base 2-line
        // frame height (the constraint `sizeToFit()` would otherwise apply).
        let measured = titleLabel.sizeThatFits(
            CGSize(width: width, height: .greatestFiniteMagnitude)
        )

        // Pin width/origin and apply the fitted height; ceil to avoid sub-point
        // truncation of the final line.
        titleLabel.frame = CGRect(
            x: origin.x, y: origin.y, width: width, height: ceil(measured.height)
        )

        // Re-center now that the label is taller than the base 2-line layout.
        titleLabel.center.x = frame.width / 2
        titleLabel.center.y = bounds.midY

        // Compensate the fixed `.top` prepare transform for the grown height.
        // `.bottom` (and the center preset) are already height-aware upstream and
        // get `extraHeight == 0`, as does any pill that stayed at 50pt.
        let extraHeight = presentSide == .top
            ? max(0, bounds.height - Self.stockAreaHeight)
            : 0
        applyTopCompensation(extraHeight)
    }

    /// Move the view's base position (via `center.y`) and `offset` so the total
    /// compensation equals `target`, applying only the delta from what is already
    /// applied. Idempotent and non-accumulating; safe under an active transform
    /// because it never touches `frame`.
    private func applyTopCompensation(_ target: CGFloat) {
        let delta = target - appliedTopCompensation
        guard delta != 0 else { return }
        center.y -= delta
        offset += delta
        appliedTopCompensation = target
    }
}
