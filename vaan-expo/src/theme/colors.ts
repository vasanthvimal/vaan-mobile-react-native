/**
 * Colour tokens ported verbatim from `app/src/main/java/com/example/ui/theme/Color.kt`.
 * Values are kept as `#RRGGBB` strings; use {@link withAlpha} for the Compose
 * `Color.copy(alpha = …)` equivalent.
 */

// Light / Dark Palette Definitions (Vaan Consulting Tech Brand)
export const VaanMidnight = '#0F172A'; // Deep slate navy blue
export const VaanPrimaryBlue = '#1E3A8A'; // True solid corporate blue
export const VaanTeal = '#0D9488'; // Rich professional teal
export const VaanTealLight = '#14B8A6'; // Bright accents teal
export const VaanCyanAccent = '#0EA5E9'; // Modern action accent cyan
export const VaanSlateBg = '#F1F5F9'; // Clean slate gray-white background
export const VaanSlateCard = '#FFFFFF'; // Crisp pure white cards
export const VaanSlateDarkCard = '#1E293B'; // Dark mode cards slate

export const Navy80 = '#94A3B8';
export const Teal80 = '#2DD4BF';
export const Cyan80 = '#38BDF8';

/** Brand accents used inline throughout the original Compose screens. */
export const BrandEmerald = '#10B981';
export const BrandTeal = '#2DD4BF';
export const BrandSky = '#38BDF8';
export const BrandInk = '#032219'; // on-primary text used by the glassy pills
export const BrandDeepNavy = '#0B132B'; // frosted bottom bar / metric dialog canvas
export const BrandBlack = '#000000';
export const BrandWhite = '#FFFFFF';
export const SplashGradientEnd = '#020617';

const HEX_SHORT = /^#([\da-f])([\da-f])([\da-f])$/i;
const HEX_LONG = /^#([\da-f]{2})([\da-f]{2})([\da-f]{2})([\da-f]{2})?$/i;

type Rgba = { r: number; g: number; b: number; a: number };

/**
 * Parses `#RGB`, `#RRGGBB` and `#RRGGBBAA` strings, plus `rgba(...)` strings.
 * Falls back to opaque black for unparseable input rather than throwing, so a
 * bad token can never crash a render pass.
 */
export function parseColor(color: string): Rgba {
  const value = color.trim();

  const short = HEX_SHORT.exec(value);
  if (short) {
    return {
      r: parseInt(short[1]! + short[1]!, 16),
      g: parseInt(short[2]! + short[2]!, 16),
      b: parseInt(short[3]! + short[3]!, 16),
      a: 1,
    };
  }

  const long = HEX_LONG.exec(value);
  if (long) {
    return {
      r: parseInt(long[1]!, 16),
      g: parseInt(long[2]!, 16),
      b: parseInt(long[3]!, 16),
      a: long[4] === undefined ? 1 : parseInt(long[4], 16) / 255,
    };
  }

  const rgba = /^rgba?\(\s*([\d.]+)[,\s]+([\d.]+)[,\s]+([\d.]+)(?:[,\s/]+([\d.]+))?\s*\)$/i.exec(
    value,
  );
  if (rgba) {
    return {
      r: Number(rgba[1]),
      g: Number(rgba[2]),
      b: Number(rgba[3]),
      a: rgba[4] === undefined ? 1 : Number(rgba[4]),
    };
  }

  if (value.toLowerCase() === 'transparent') {
    return { r: 0, g: 0, b: 0, a: 0 };
  }

  return { r: 0, g: 0, b: 0, a: 1 };
}

const clamp01 = (n: number) => (n < 0 ? 0 : n > 1 ? 1 : n);

/** Compose equivalent: `color.copy(alpha = alpha)`. */
export function withAlpha(color: string, alpha: number): string {
  const { r, g, b } = parseColor(color);
  return `rgba(${r}, ${g}, ${b}, ${clamp01(alpha)})`;
}

/** Fully transparent version of a colour — keeps hue for smooth gradient ramps. */
export const fadeOut = (color: string): string => withAlpha(color, 0);

/** Compose `Color.Transparent`. Gradients must fade the *same* hue to avoid a grey halo. */
export const Transparent = 'transparent';

/**
 * Compose renders `Color.Transparent` inside a gradient as premultiplied
 * transparent-black, which on iOS/Android RN gradients shows a grey edge.
 * Using the source hue at alpha 0 reproduces Compose's visual result exactly.
 */
export const gradientFade = (color: string): string => withAlpha(color, 0);
