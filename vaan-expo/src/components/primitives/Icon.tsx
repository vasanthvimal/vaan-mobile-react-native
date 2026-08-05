import React, { memo } from 'react';
import MaterialIcons from '@expo/vector-icons/MaterialIcons';
import type { StyleProp, TextStyle } from 'react-native';

/**
 * `androidx.compose.material3.Icon` + `Icons.Default.*` equivalent.
 *
 * `@expo/vector-icons/MaterialIcons` is backed by the *same* Google Material
 * Icons font that Compose's `Icons.Filled` set is generated from, so glyphs are
 * identical rather than look-alikes. Compose exposes them in PascalCase
 * (`Icons.Default.CloudQueue`) while the font uses kebab-case (`cloud-queue`);
 * {@link composeIconName} performs that conversion so call sites can keep using
 * the original Kotlin names and stay greppable against the source.
 */

export type MaterialIconName = React.ComponentProps<typeof MaterialIcons>['name'];

const OVERRIDES: Record<string, MaterialIconName> = {
  // Compose auto-mirrored icons live under Icons.AutoMirrored.Filled.*
  ArrowForward: 'arrow-forward',
  ArrowBack: 'arrow-back',
  Send: 'send',
  Login: 'login',
  Logout: 'logout',
  List: 'list',
  Label: 'label',
  Article: 'article',
  Chat: 'chat',
  Message: 'message',
  Note: 'note',
  Help: 'help',
};

export function composeIconName(name: string): MaterialIconName {
  const override = OVERRIDES[name];
  if (override) return override;

  return name
    .replace(/([a-z0-9])([A-Z])/g, '$1-$2')
    .replace(/([A-Z]+)([A-Z][a-z])/g, '$1-$2')
    .toLowerCase() as MaterialIconName;
}

export type IconProps = {
  /** Either a Compose name ("CheckCircle") or a raw font name ("check-circle"). */
  name: string;
  size: number;
  color: string;
  style?: StyleProp<TextStyle>;
  /** Maps to Compose's `contentDescription`; `null` marks the icon decorative. */
  contentDescription?: string | null;
};

export const Icon = memo(function Icon({
  name,
  size,
  color,
  style,
  contentDescription,
}: IconProps) {
  const decorative = contentDescription == null;

  return (
    <MaterialIcons
      name={name.includes('-') || name === name.toLowerCase() ? (name as MaterialIconName) : composeIconName(name)}
      size={size}
      color={color}
      style={style}
      accessible={!decorative}
      accessibilityRole={decorative ? 'none' : 'image'}
      accessibilityLabel={contentDescription ?? undefined}
      importantForAccessibility={decorative ? 'no' : 'yes'}
      allowFontScaling={false}
    />
  );
});
