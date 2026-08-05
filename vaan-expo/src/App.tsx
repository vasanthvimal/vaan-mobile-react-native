import React, { useEffect, useMemo, useState } from 'react';
import {
  Alert,
  Animated,
  Easing,
  KeyboardAvoidingView,
  Linking,
  Modal,
  Platform,
  Pressable,
  SafeAreaView,
  ScrollView,
  StatusBar,
  StyleSheet,
  Switch,
  TextInput,
  View,
} from 'react-native';

import { AppText, HorizontalDivider, Spacer, elevationStyle } from '@/components/primitives/AppText';
import { Gradient, GradientBorder, RadialGlow } from '@/components/primitives/Gradients';
import { Icon } from '@/components/primitives/Icon';
import { articlesList, servicesList, type TechArticle, type VaanService } from '@/content/content';
import type { Appointment, ClientInquiry, ClientMeeting, EmailLog } from '@/data/types';
import { useInteraction } from '@/hooks/useInteraction';
import { useAppStore } from '@/store/appStore';
import { useColors, VaanThemeProvider, withAlpha } from '@/theme';
import {
  formatCardDateTime,
  formatLongDate,
  formatLongDateTime,
  formatMeetingDateTime,
  formatOutboxDateTime,
  formatTimeOnly,
} from '@/utils/format';

type RootTab = 'home' | 'services' | 'insights' | 'chatbot' | 'bookings';
type PortalTab = 'book' | 'contact';
type HomeDialog = 'pillars' | 'capabilities' | 'meetings' | 'inquiries' | 'bookings' | 'outbox';

type HomePillar = {
  id: string;
  num: string;
  title: string;
  desc: string;
  accent: string;
  detail: string;
};

type HomeCapability = {
  id: string;
  title: string;
  summary: string;
  accent: string;
};

const HOME_PILLARS: HomePillar[] = [
  {
    id: 'strategic-ownership',
    num: '01',
    title: 'Strategic ownership',
    desc: 'Complex business requirements translated into scalable, secure, cost-effective architectures.',
    accent: '#38BDF8',
    detail:
      'Bridging executive vision with production engineering accountability: target architecture, FinOps governance, and zero-trust controls embedded from day zero.',
  },
  {
    id: 'delivery-confidence',
    num: '02',
    title: 'Delivery confidence',
    desc: 'Full lifecycle delivery led against business and regulatory standards.',
    accent: '#34D399',
    detail:
      'SAFe-aligned execution with DevSecOps quality gates, milestone rollouts, and measurable technical debt remediation across enterprise teams.',
  },
  {
    id: 'technical-credibility',
    num: '03',
    title: 'Technical credibility',
    desc: 'Hands-on cloud, data and distributed-systems expertise that reduces delivery risk.',
    accent: '#A78BFA',
    detail:
      'Production-tested architecture and platform engineering across AWS, Azure, GCP, Snowflake, Databricks, Microsoft Fabric, and event-driven microservices.',
  },
];

const HOME_CAPABILITIES: HomeCapability[] = [
  {
    id: 'multi-cloud',
    title: 'Multi-cloud architecture',
    summary: 'AWS, Azure, and GCP blueprints with security and FinOps guardrails.',
    accent: '#38BDF8',
  },
  {
    id: 'data-modernisation',
    title: 'Data platform modernisation',
    summary: 'Lakehouse, streaming, and governed analytics with Snowflake and Databricks.',
    accent: '#34D399',
  },
  {
    id: 'distributed-systems',
    title: 'Distributed systems & microservices',
    summary: 'Resilient, event-driven systems for high-throughput enterprise workloads.',
    accent: '#FB923C',
  },
  {
    id: 'governance-leadership',
    title: 'Governance & delivery leadership',
    summary: 'SAFe, compliance readiness, and cross-team execution alignment.',
    accent: '#A78BFA',
  },
];

function VaanBrandLogo({ size = 32 }: { size?: number }) {
  const interaction = useInteraction();
  const pulseAlpha = interaction.active ? 0.7 : 0.45;

  return (
    <Pressable {...interaction.handlers} style={{ width: size, height: size }}>
      <RadialGlow
        colors={[
          withAlpha('#2DD4BF', pulseAlpha * 0.65),
          withAlpha('#10B981', pulseAlpha * 0.25),
          'transparent',
        ]}
        borderRadius={22}
        style={{ borderRadius: 22 }}
      />
      <Gradient
        colors={[withAlpha('#FFFFFF', 0.3), 'transparent']}
        style={{
          width: size,
          height: size,
          borderRadius: 20,
          overflow: 'hidden',
          borderWidth: 1,
          borderColor: withAlpha('#FFFFFF', 0.12),
        }}
      >
        <View
          style={{
            flex: 1,
            backgroundColor: '#0F172A',
            borderRadius: 20,
            overflow: 'hidden',
          }}
        >
          <Gradient colors={['#2DD4BF', '#10B981']} direction="diagonal" style={{ flex: 1, opacity: 0.3 }} />
          <View style={[StyleSheet.absoluteFill, { alignItems: 'center', justifyContent: 'center' }]}>
            <Icon name="Cloud" size={size * 0.54} color="#FFFFFF" contentDescription="Vaan icon" />
          </View>
        </View>
      </Gradient>
    </Pressable>
  );
}

function VaanBrandText({ size = 15 }: { size?: number }) {
  return (
    <AppText fontSize={size} fontWeight="800" letterSpacing={1} color="#FFFFFF">
      VAAN
      <AppText fontSize={size} fontWeight="800" color="#2DD4BF">
        .
      </AppText>
      CONSULTING
    </AppText>
  );
}

function GlassyChip({
  text,
  selected,
  onPress,
}: {
  text: string;
  selected: boolean;
  onPress: () => void;
}) {
  return (
    <Pressable onPress={onPress} style={{ marginRight: 8, marginBottom: 8 }}>
      <GradientBorder
        width={selected ? 1.5 : 1}
        borderRadius={14}
        colors={
          selected
            ? [withAlpha('#FFFFFF', 0.9), '#2DD4BF', withAlpha('#10B981', 0.85)]
            : [withAlpha('#FFFFFF', 0.25), withAlpha('#FFFFFF', 0.08)]
        }
      >
        <Gradient
          colors={selected ? ['#2DD4BF', '#10B981'] : [withAlpha('#FFFFFF', 0.12), withAlpha('#FFFFFF', 0.04)]}
          style={{ paddingHorizontal: 14, paddingVertical: 8, borderRadius: 13 }}
        >
          <AppText fontSize={11} fontWeight={selected ? '800' : '700'} color={selected ? '#032219' : '#CAC4D0'}>
            {text}
          </AppText>
        </Gradient>
      </GradientBorder>
    </Pressable>
  );
}

function GlassyButton({
  text,
  onPress,
  primary = true,
}: {
  text: string;
  onPress: () => void;
  primary?: boolean;
}) {
  return (
    <Pressable onPress={onPress}>
      <GradientBorder
        width={1.2}
        borderRadius={14}
        colors={
          primary
            ? [withAlpha('#FFFFFF', 0.9), '#2DD4BF', withAlpha('#FFFFFF', 0.35)]
            : [withAlpha('#FFFFFF', 0.7), withAlpha('#2DD4BF', 0.45), withAlpha('#FFFFFF', 0.15)]
        }
      >
        <Gradient
          colors={primary ? ['#2DD4BF', '#10B981'] : [withAlpha('#FFFFFF', 0.14), withAlpha('#FFFFFF', 0.05)]}
          style={{ paddingHorizontal: 16, paddingVertical: 12, borderRadius: 13, alignItems: 'center' }}
        >
          <AppText fontSize={12} fontWeight="800" color={primary ? '#032219' : '#FFFFFF'}>
            {text}
          </AppText>
        </Gradient>
      </GradientBorder>
    </Pressable>
  );
}

function TopBar({ autoEmailEnabled }: { autoEmailEnabled: boolean }) {
  const colors = useColors();
  return (
    <View style={[styles.topBar, { backgroundColor: colors.background }]}> 
      <View style={{ flexDirection: 'row', alignItems: 'center', flex: 1 }}>
        <VaanBrandLogo size={32} />
        <Spacer w={8} />
        <View>
          <VaanBrandText size={15} />
          <AppText fontSize={10} fontWeight="700" color={colors.secondary}>
            Enterprise Cloud & Data Hub
          </AppText>
        </View>
      </View>
      <View
        style={{
          paddingHorizontal: 12,
          paddingVertical: 6,
          borderRadius: 16,
          backgroundColor: withAlpha(colors.secondary, 0.12),
          flexDirection: 'row',
          alignItems: 'center',
        }}
      >
        <View
          style={{
            width: 8,
            height: 8,
            borderRadius: 4,
            backgroundColor: autoEmailEnabled ? '#10B981' : '#94A3B8',
            marginRight: 6,
          }}
        />
        <AppText
          fontSize={11}
          fontWeight="700"
          color={autoEmailEnabled ? colors.secondary : colors.onSurfaceVariant}
        >
          {autoEmailEnabled ? 'SMTP Online' : 'SMTP Paused'}
        </AppText>
      </View>
    </View>
  );
}

function BottomBar({ tab, onChange }: { tab: RootTab; onChange: (tab: RootTab) => void }) {
  const items = [
    { id: 'home', label: 'Home', icon: 'Dashboard' },
    { id: 'services', label: 'Services', icon: 'CloudQueue' },
    { id: 'insights', label: 'Blog', icon: 'Language' },
    { id: 'chatbot', label: 'VaanAI Chat', icon: 'Chat' },
    { id: 'bookings', label: 'Bookings', icon: 'Schedule' },
  ] as const;

  return (
    <View style={{ paddingHorizontal: 10, paddingVertical: 6 }}>
      <GradientBorder
        width={1.25}
        borderRadius={32}
        colors={[withAlpha('#FFFFFF', 0.45), withAlpha('#2DD4BF', 0.25), withAlpha('#FFFFFF', 0.12)]}
        innerColor={withAlpha('#0B132B', 0.87)}
        style={[elevationStyle(16), { borderRadius: 32 }]}
      >
        <View style={{ flexDirection: 'row', paddingHorizontal: 4, paddingVertical: 6 }}>
          {items.map((item) => {
            const selected = tab === item.id;
            return (
              <Pressable
                key={item.id}
                onPress={() => onChange(item.id)}
                style={{
                  flex: 1,
                  alignItems: 'center',
                  borderRadius: 22,
                  paddingVertical: 8,
                  backgroundColor: selected ? withAlpha('#2DD4BF', 0.2) : 'transparent',
                  borderWidth: selected ? 1 : 0,
                  borderColor: selected ? withAlpha('#2DD4BF', 0.45) : 'transparent',
                }}
              >
                <Icon
                  name={item.icon}
                  size={20}
                  color={selected ? '#2DD4BF' : withAlpha('#94A3B8', 0.65)}
                  contentDescription={item.label}
                />
                <Spacer h={4} />
                <AppText
                  fontSize={9}
                  fontWeight={selected ? '800' : '500'}
                  color={selected ? '#FFFFFF' : withAlpha('#94A3B8', 0.65)}
                >
                  {item.label}
                </AppText>
                {selected ? <View style={{ width: 4, height: 4, borderRadius: 2, backgroundColor: '#2DD4BF', marginTop: 3 }} /> : null}
              </Pressable>
            );
          })}
        </View>
      </GradientBorder>
    </View>
  );
}

function Splash({ onDismiss }: { onDismiss: () => void }) {
  const spinner = React.useRef(new Animated.Value(0)).current;

  useEffect(() => {
    const animation = Animated.loop(
      Animated.timing(spinner, {
        toValue: 1,
        duration: 1100,
        easing: Easing.linear,
        useNativeDriver: true,
      }),
    );
    animation.start();
    return () => animation.stop();
  }, [spinner]);

  const rotate = spinner.interpolate({
    inputRange: [0, 1],
    outputRange: ['0deg', '360deg'],
  });

  return (
    <Pressable onPress={onDismiss} style={{ flex: 1 }}>
      <Gradient colors={['#0F172A', '#020617']} style={styles.splashRoot}>
        <VaanBrandLogo size={96} />
        <Spacer h={24} />
        <VaanBrandText size={28} />
        <Spacer h={8} />
        <AppText fontSize={13} fontWeight="500" color="#2DD4BF" textAlign="center" lineHeight={18}>
          Enterprise cloud & data, engineered to move.
        </AppText>
        <Spacer h={40} />
        <Animated.View style={[styles.loader, { transform: [{ rotate }] }]} />
      </Gradient>
    </Pressable>
  );
}

function DialogSheet({
  visible,
  title,
  onClose,
  children,
}: {
  visible: boolean;
  title: string;
  onClose: () => void;
  children: React.ReactNode;
}) {
  return (
    <Modal visible={visible} animationType="fade" transparent onRequestClose={onClose}>
      <View style={styles.overlayBackdrop}>
        <View style={styles.overlayCard}>
          <View style={{ flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center' }}>
            <AppText fontSize={16} fontWeight="800" color="#FFFFFF">{title}</AppText>
            <Pressable onPress={onClose}>
              <Icon name="Close" size={20} color="#FFFFFF" contentDescription="Close" />
            </Pressable>
          </View>
          <Spacer h={10} />
          <HorizontalDivider color={withAlpha('#FFFFFF', 0.14)} />
          <Spacer h={10} />
          <View style={{ maxHeight: '82%' }}>{children}</View>
        </View>
      </View>
    </Modal>
  );
}

function HomeScreen({
  meetings,
  inquiries,
  appointments,
  emailLogs,
  onNavigate,
  onOpenPortalTab,
}: {
  meetings: ClientMeeting[];
  inquiries: ClientInquiry[];
  appointments: Appointment[];
  emailLogs: EmailLog[];
  onNavigate: (tab: RootTab) => void;
  onOpenPortalTab: (tab: PortalTab) => void;
}) {
  const colors = useColors();
  const aiDraftState = useAppStore((s) => s.aiDraftState);
  const resetAiState = useAppStore((s) => s.resetAiState);
  const generateAIEmailDraftForInquiry = useAppStore((s) => s.generateAIEmailDraftForInquiry);
  const submitInquiryReply = useAppStore((s) => s.submitInquiryReply);

  const [dialog, setDialog] = useState<HomeDialog | null>(null);
  const [selectedInquiry, setSelectedInquiry] = useState<ClientInquiry | null>(null);
  const [replyText, setReplyText] = useState('');

  useEffect(() => {
    if (aiDraftState.kind === 'success') {
      setReplyText(aiDraftState.draft);
    }
  }, [aiDraftState]);

  const metrics = [
    { label: 'Meetings', value: meetings.length.toString(), icon: 'Schedule', color: '#2DD4BF' },
    { label: 'Inquiries', value: inquiries.length.toString(), icon: 'Mail', color: '#38BDF8' },
    { label: 'Bookings', value: appointments.length.toString(), icon: 'Event', color: '#34D399' },
    { label: 'Outbox', value: emailLogs.length.toString(), icon: 'Send', color: '#FB923C' },
  ];

  return (
    <ScrollView contentContainerStyle={{ padding: 16, paddingBottom: 26 }}>
      <GradientBorder
        width={1.2}
        borderRadius={20}
        colors={[withAlpha('#2DD4BF', 0.45), withAlpha('#38BDF8', 0.25)]}
        innerColor={colors.surface}
      >
        <Gradient colors={[withAlpha('#2DD4BF', 0.2), 'transparent']} style={{ padding: 20, borderRadius: 18 }}>
          <AppText fontSize={24} fontWeight="900">Enterprise Cloud & Data Strategy</AppText>
          <Spacer h={8} />
          <AppText fontSize={13} lineHeight={19} color={colors.onSurfaceVariant}>
            VAAN Consulting transforms cloud platforms, modern data stacks, and product delivery pipelines for regulated enterprises.
          </AppText>
          <Spacer h={16} />
          <View style={{ flexDirection: 'row', gap: 8 }}>
            <View style={{ flex: 1 }}>
              <GlassyButton text="Explore Services" onPress={() => onNavigate('services')} />
            </View>
            <View style={{ flex: 1 }}>
              <GlassyButton text="Book Discovery" onPress={() => onNavigate('bookings')} primary={false} />
            </View>
          </View>
        </Gradient>
      </GradientBorder>

      <Spacer h={16} />
      <View style={{ flexDirection: 'row', flexWrap: 'wrap', gap: 10 }}>
        {metrics.map((m) => (
          <Pressable key={m.label} onPress={() => setDialog(m.label.toLowerCase() as HomeDialog)} style={[{ width: '48%', borderRadius: 14, backgroundColor: colors.surface, padding: 14 }, elevationStyle(2)]}>
            <View style={{ flexDirection: 'row', alignItems: 'center', justifyContent: 'space-between' }}>
              <AppText fontSize={11} fontWeight="700" color={colors.onSurfaceVariant}>{m.label}</AppText>
              <Icon name={m.icon} size={16} color={m.color} contentDescription={m.label} />
            </View>
            <Spacer h={6} />
            <AppText fontSize={24} fontWeight="900" color={m.color}>{m.value}</AppText>
          </Pressable>
        ))}
      </View>

      <Spacer h={18} />
      <View style={[styles.formCard, { backgroundColor: '#0F1A2E' }]}>
        <AppText fontSize={10} letterSpacing={1} fontWeight="700" color="#14B8A6">ABOUT VAAN</AppText>
        <Spacer h={8} />
        <AppText fontSize={19} fontWeight="900">Architecture that survives contact with production.</AppText>
        <Spacer h={10} />
        <AppText fontSize={12} color={colors.onSurfaceVariant} lineHeight={18}>
          The work sits end-to-end: cloud strategy, distributed system design, and non-functional requirements that regulated businesses cannot compromise on.
        </AppText>
        <Spacer h={14} />
        <Pressable onPress={() => setDialog('pillars')} style={[styles.dialogAction, { borderColor: withAlpha('#38BDF8', 0.35) }]}>
          <AppText fontSize={12} fontWeight="700" color="#38BDF8">Open Value Pillars</AppText>
        </Pressable>
        <Spacer h={8} />
        <Pressable onPress={() => setDialog('capabilities')} style={[styles.dialogAction, { borderColor: withAlpha('#2DD4BF', 0.35) }]}>
          <AppText fontSize={12} fontWeight="700" color="#2DD4BF">Open Capability Brief</AppText>
        </Pressable>
      </View>

      <DialogSheet
        visible={dialog === 'pillars'}
        title="Value Pillars"
        onClose={() => setDialog(null)}
      >
        <ScrollView>
          {HOME_PILLARS.map((pillar) => (
            <View key={pillar.id} style={[styles.formCard, { marginBottom: 10 }]}>
              <AppText fontSize={11} fontWeight="800" color={pillar.accent}>{pillar.num} • {pillar.title}</AppText>
              <Spacer h={4} />
              <AppText fontSize={12} color={colors.onSurface} lineHeight={17}>{pillar.desc}</AppText>
              <Spacer h={5} />
              <AppText fontSize={11} color={colors.onSurfaceVariant} lineHeight={16}>{pillar.detail}</AppText>
            </View>
          ))}
        </ScrollView>
      </DialogSheet>

      <DialogSheet
        visible={dialog === 'capabilities'}
        title="Capability Brief"
        onClose={() => setDialog(null)}
      >
        <ScrollView>
          {HOME_CAPABILITIES.map((item) => (
            <View key={item.id} style={[styles.formCard, { marginBottom: 10 }]}>
              <AppText fontSize={13} fontWeight="800" color={item.accent}>{item.title}</AppText>
              <Spacer h={4} />
              <AppText fontSize={12} color={colors.onSurfaceVariant} lineHeight={17}>{item.summary}</AppText>
            </View>
          ))}
          <Spacer h={6} />
          <GlassyButton text="Book Discovery Session" onPress={() => {
            setDialog(null);
            onNavigate('bookings');
          }} />
        </ScrollView>
      </DialogSheet>

      <DialogSheet
        visible={dialog === 'meetings'}
        title="Meeting Overview"
        onClose={() => setDialog(null)}
      >
        <ScrollView>
          {meetings.length === 0 ? <AppText fontSize={12} color={colors.onSurfaceVariant}>No meetings scheduled.</AppText> : meetings.map((meeting) => <MeetingCard key={meeting.id} meeting={meeting} onCancel={() => undefined} />)}
        </ScrollView>
      </DialogSheet>

      <DialogSheet
        visible={dialog === 'bookings'}
        title="Consulting Bookings"
        onClose={() => setDialog(null)}
      >
        <ScrollView>
          {appointments.length === 0 ? <AppText fontSize={12} color={colors.onSurfaceVariant}>No bookings currently registered.</AppText> : appointments.map((appt) => <AppointmentCard key={appt.id} appt={appt} onConfirm={() => undefined} onComplete={() => undefined} />)}
          <Spacer h={8} />
          <GlassyButton text="Open Bookings Portal" onPress={() => {
            setDialog(null);
            onNavigate('bookings');
          }} />
        </ScrollView>
      </DialogSheet>

      <DialogSheet
        visible={dialog === 'outbox'}
        title="Outbox Activity"
        onClose={() => setDialog(null)}
      >
        <ScrollView>
          {emailLogs.length === 0 ? <AppText fontSize={12} color={colors.onSurfaceVariant}>No outbound notifications yet.</AppText> : emailLogs.map((log) => <OutboxItem key={log.id} log={log} />)}
        </ScrollView>
      </DialogSheet>

      <DialogSheet
        visible={dialog === 'inquiries'}
        title="Website Inquiries"
        onClose={() => {
          setDialog(null);
          setSelectedInquiry(null);
          setReplyText('');
          resetAiState();
        }}
      >
        {selectedInquiry ? (
          <ScrollView>
            <View style={styles.formCard}>
              <AppText fontSize={10} letterSpacing={1} fontWeight="700" color="#38BDF8">CLIENT BRIEF</AppText>
              <Spacer h={6} />
              <AppText fontSize={12} fontWeight="700">{selectedInquiry.clientName} ({selectedInquiry.clientEmail})</AppText>
              <AppText fontSize={12} color={colors.onSurfaceVariant}>{selectedInquiry.companyName}</AppText>
              <Spacer h={6} />
              <AppText fontSize={13} fontWeight="800">{selectedInquiry.subject}</AppText>
              <AppText fontSize={12} color={colors.onSurfaceVariant} lineHeight={17}>{selectedInquiry.message}</AppText>
            </View>
            <Spacer h={10} />
            <View style={styles.formCard}>
              <View style={{ flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center' }}>
                <AppText fontSize={12} fontWeight="800" color="#2DD4BF">Vaan AI Draft Assistant</AppText>
                <Pressable onPress={() => void generateAIEmailDraftForInquiry(selectedInquiry)}>
                  <AppText fontSize={11} fontWeight="700" color="#38BDF8">Draft Response</AppText>
                </Pressable>
              </View>
              <Spacer h={6} />
              <AppText fontSize={11} color={colors.onSurfaceVariant}>
                {aiDraftState.kind === 'loading'
                  ? 'Consulting Gemini models to formulate a professional reply...'
                  : 'Generate and refine a professional response before sending SMTP notification.'}
              </AppText>
              <Spacer h={8} />
              <TextInput
                value={replyText}
                onChangeText={setReplyText}
                placeholder="Email Reply Body"
                placeholderTextColor={withAlpha(colors.onSurfaceVariant, 0.8)}
                style={{ minHeight: 120, borderRadius: 10, borderWidth: 1, borderColor: withAlpha(colors.onSurfaceVariant, 0.25), color: colors.onSurface, paddingHorizontal: 10, paddingVertical: 8 }}
                multiline
              />
              <Spacer h={8} />
              <View style={{ flexDirection: 'row', justifyContent: 'space-between', gap: 10 }}>
                <View style={{ flex: 1 }}>
                  <GlassyButton text="Back" primary={false} onPress={() => {
                    setSelectedInquiry(null);
                    setReplyText('');
                    resetAiState();
                  }} />
                </View>
                <View style={{ flex: 1 }}>
                  <GlassyButton text="Send SMTP" onPress={() => {
                    if (!replyText.trim()) {
                      Alert.alert('Validation', 'Reply body cannot be empty');
                      return;
                    }
                    void submitInquiryReply(selectedInquiry, replyText.trim());
                    setSelectedInquiry(null);
                    setReplyText('');
                    resetAiState();
                  }} />
                </View>
              </View>
            </View>
          </ScrollView>
        ) : (
          <ScrollView>
            {inquiries.length === 0 ? (
              <AppText fontSize={12} color={colors.onSurfaceVariant}>No inquiries synchronized.</AppText>
            ) : (
              inquiries.map((inquiry) => (
                <Pressable key={inquiry.id} onPress={() => setSelectedInquiry(inquiry)}>
                  <View style={[styles.formCard, { marginBottom: 10 }]}>
                    <AppText fontSize={10} fontWeight="700" color="#38BDF8">{inquiry.companyName}</AppText>
                    <Spacer h={3} />
                    <AppText fontSize={13} fontWeight="800">{inquiry.subject}</AppText>
                    <Spacer h={3} />
                    <AppText fontSize={12} color={colors.onSurfaceVariant} lineHeight={16} numberOfLines={2}>{inquiry.message}</AppText>
                    <Spacer h={6} />
                    <AppText fontSize={11} color={colors.onSurfaceVariant}>From: {inquiry.clientName} • {formatCardDateTime(inquiry.receivedTime)}</AppText>
                  </View>
                </Pressable>
              ))
            )}
            <Spacer h={8} />
            <GlassyButton text="Open Contact Us" primary={false} onPress={() => {
              setDialog(null);
              onNavigate('bookings');
              onOpenPortalTab('contact');
            }} />
          </ScrollView>
        )}
      </DialogSheet>
    </ScrollView>
  );
}

function ServicesScreen({
  bookmarked,
  onToggleBookmark,
  onBookService,
}: {
  bookmarked: ReadonlySet<string>;
  onToggleBookmark: (id: string) => void;
  onBookService: (title: string) => void;
}) {
  const colors = useColors();

  return (
    <ScrollView contentContainerStyle={{ padding: 16, paddingBottom: 30 }}>
      {servicesList.map((service) => {
        const isBookmarked = bookmarked.has(service.id);
        return (
          <View key={service.id} style={{ marginBottom: 14 }}>
            <GradientBorder
              width={1.25}
              borderRadius={20}
              colors={[withAlpha(service.accentColor, 0.9), withAlpha(service.accentColor, 0.35)]}
              innerColor={colors.surface}
            >
              <Gradient colors={[withAlpha(service.accentColor, 0.18), 'transparent']} style={{ padding: 18, borderRadius: 19 }}>
                <View style={{ flexDirection: 'row', alignItems: 'center' }}>
                  <View style={{ width: 40, height: 40, borderRadius: 8, backgroundColor: withAlpha(service.accentColor, 0.18), alignItems: 'center', justifyContent: 'center' }}>
                    <Icon name="Cloud" size={20} color={service.accentColor} contentDescription={service.title} />
                  </View>
                  <Spacer w={12} />
                  <View style={{ flex: 1 }}>
                    <AppText fontSize={16} fontWeight="800" color={service.accentColor}>{service.title}</AppText>
                    <AppText fontSize={11} color={colors.onSurfaceVariant}>{service.subtitle}</AppText>
                  </View>
                  <Pressable onPress={() => onToggleBookmark(service.id)}>
                    <Icon name={isBookmarked ? 'Bookmark' : 'BookmarkBorder'} size={20} color={service.accentColor} contentDescription="Bookmark" />
                  </Pressable>
                </View>
                <Spacer h={10} />
                <AppText fontSize={12} color={colors.onSurfaceVariant} lineHeight={17}>{service.description}</AppText>
                <Spacer h={10} />
                <View style={{ flexDirection: 'row', flexWrap: 'wrap' }}>
                  {service.platforms.map((p) => (
                    <View key={p} style={{ borderRadius: 6, borderWidth: 1, borderColor: withAlpha(service.accentColor, 0.35), backgroundColor: withAlpha(service.accentColor, 0.08), paddingHorizontal: 8, paddingVertical: 4, marginRight: 6, marginBottom: 6 }}>
                      <AppText fontSize={10} fontWeight="700" color={service.accentColor}>{p}</AppText>
                    </View>
                  ))}
                </View>
                <Spacer h={8} />
                <GlassyButton text="Book this service" onPress={() => onBookService(service.title)} />
              </Gradient>
            </GradientBorder>
          </View>
        );
      })}
    </ScrollView>
  );
}

function InsightsScreen({ bookmarked, onToggleBookmark }: { bookmarked: ReadonlySet<string>; onToggleBookmark: (id: string) => void }) {
  const colors = useColors();
  const [selected, setSelected] = useState<TechArticle | null>(null);

  return (
    <>
      <ScrollView contentContainerStyle={{ padding: 16, paddingBottom: 26 }}>
        {articlesList.map((article) => {
          const isBookmarked = bookmarked.has(article.id);
          return (
            <Pressable key={article.id} onPress={() => setSelected(article)} style={{ marginBottom: 12 }}>
              <View style={{ borderRadius: 14, padding: 14, backgroundColor: colors.surface }}>
                <View style={{ flexDirection: 'row', justifyContent: 'space-between' }}>
                  <AppText fontSize={11} fontWeight="700" color="#38BDF8">{article.category}</AppText>
                  <Pressable onPress={() => onToggleBookmark(article.id)}>
                    <Icon name={isBookmarked ? 'Bookmark' : 'BookmarkBorder'} size={18} color="#38BDF8" contentDescription="Bookmark" />
                  </Pressable>
                </View>
                <Spacer h={5} />
                <AppText fontSize={15} fontWeight="800">{article.title}</AppText>
                <Spacer h={6} />
                <AppText fontSize={12} color={colors.onSurfaceVariant} lineHeight={17}>{article.summary}</AppText>
                <Spacer h={8} />
                <AppText fontSize={10} color={colors.onSurfaceVariant}>{article.date} • {article.readTime}</AppText>
              </View>
            </Pressable>
          );
        })}
      </ScrollView>

      <Modal visible={selected != null} animationType="slide" onRequestClose={() => setSelected(null)}>
        <SafeAreaView style={{ flex: 1, backgroundColor: '#0F172A' }}>
          <View style={{ flexDirection: 'row', alignItems: 'center', paddingHorizontal: 16, paddingVertical: 12 }}>
            <Pressable onPress={() => setSelected(null)}>
              <Icon name="Close" size={22} color="#FFFFFF" contentDescription="Close" />
            </Pressable>
            <Spacer w={10} />
            <AppText fontSize={15} fontWeight="800" color="#FFFFFF" numberOfLines={1}>
              {selected?.title}
            </AppText>
          </View>
          <HorizontalDivider color={withAlpha('#FFFFFF', 0.16)} />
          <ScrollView contentContainerStyle={{ padding: 16 }}>
            <AppText fontSize={13} color="#2DD4BF" fontWeight="700">{selected?.category}</AppText>
            <Spacer h={8} />
            <AppText fontSize={13} color="#FFFFFF" lineHeight={20}>{selected?.content}</AppText>
          </ScrollView>
        </SafeAreaView>
      </Modal>
    </>
  );
}

function ChatScreen() {
  const messages = useAppStore((s) => s.chatMessages);
  const isLoading = useAppStore((s) => s.isChatLoading);
  const send = useAppStore((s) => s.sendChatMessage);
  const clear = useAppStore((s) => s.clearChatHistory);
  const colors = useColors();
  const [text, setText] = useState('');

  return (
    <KeyboardAvoidingView style={{ flex: 1 }} behavior={Platform.OS === 'ios' ? 'padding' : undefined}>
      <View style={{ flex: 1, padding: 16 }}>
        <View style={{ flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center' }}>
          <AppText fontSize={16} fontWeight="800">VaanAI</AppText>
          <Pressable onPress={clear}>
            <AppText fontSize={12} fontWeight="700" color="#38BDF8">Clear Chat</AppText>
          </Pressable>
        </View>
        <Spacer h={10} />
        <ScrollView style={{ flex: 1 }} contentContainerStyle={{ paddingBottom: 20 }}>
          {messages.map((m, i) => (
            <View key={`${m.role}-${i}`} style={{ alignSelf: m.role === 'user' ? 'flex-end' : 'flex-start', maxWidth: '86%', marginBottom: 8 }}>
              <View style={{ borderRadius: 12, backgroundColor: m.role === 'user' ? withAlpha('#2DD4BF', 0.2) : colors.surface, paddingHorizontal: 12, paddingVertical: 10 }}>
                <AppText fontSize={12} lineHeight={18} color={m.role === 'user' ? '#E2FDF5' : colors.onSurface}>{m.text}</AppText>
              </View>
            </View>
          ))}
          {isLoading ? <AppText fontSize={12} color={colors.onSurfaceVariant}>VaanAI is thinking...</AppText> : null}
        </ScrollView>

        <View style={{ flexDirection: 'row', alignItems: 'flex-end', marginTop: 8 }}>
          <TextInput
            value={text}
            onChangeText={setText}
            placeholder="Ask about cloud, data, architecture..."
            placeholderTextColor={withAlpha(colors.onSurfaceVariant, 0.8)}
            style={{ flex: 1, borderRadius: 12, backgroundColor: colors.surface, borderWidth: 1, borderColor: withAlpha(colors.onSurfaceVariant, 0.25), color: colors.onSurface, paddingHorizontal: 12, paddingVertical: 10, minHeight: 44 }}
            multiline
          />
          <Spacer w={8} />
          <Pressable
            onPress={() => {
              const value = text;
              setText('');
              void send(value);
            }}
            style={{ width: 44, height: 44, borderRadius: 22, backgroundColor: '#2DD4BF', alignItems: 'center', justifyContent: 'center' }}
          >
            <Icon name="Send" size={20} color="#032219" contentDescription="Send" />
          </Pressable>
        </View>
      </View>
    </KeyboardAvoidingView>
  );
}

function MeetingCard({ meeting, onCancel }: { meeting: ClientMeeting; onCancel: () => void }) {
  const colors = useColors();
  return (
    <View style={{ borderRadius: 12, backgroundColor: colors.surface, padding: 12, marginBottom: 10 }}>
      <AppText fontSize={14} fontWeight="800">{meeting.title}</AppText>
      <AppText fontSize={12} color={colors.onSurfaceVariant}>{meeting.clientName} • {meeting.clientEmail}</AppText>
      <Spacer h={4} />
      <AppText fontSize={12} color="#38BDF8">{formatMeetingDateTime(meeting.dateTime)}</AppText>
      <AppText fontSize={12} color={colors.onSurfaceVariant}>Status: {meeting.status}</AppText>
      {meeting.status === 'Scheduled' ? (
        <Pressable onPress={onCancel} style={{ marginTop: 8, alignSelf: 'flex-start' }}>
          <AppText fontSize={12} fontWeight="700" color="#FB923C">Cancel Meeting</AppText>
        </Pressable>
      ) : null}
    </View>
  );
}

function InquiryCard({ inquiry, onReply }: { inquiry: ClientInquiry; onReply: (reply: string) => void }) {
  const colors = useColors();
  const [reply, setReply] = useState('');
  return (
    <View style={{ borderRadius: 12, backgroundColor: colors.surface, padding: 12, marginBottom: 10 }}>
      <AppText fontSize={14} fontWeight="800">{inquiry.subject}</AppText>
      <AppText fontSize={12} color={colors.onSurfaceVariant}>{inquiry.clientName} • {inquiry.companyName}</AppText>
      <AppText fontSize={12} color={colors.onSurfaceVariant} lineHeight={17}>{inquiry.message}</AppText>
      <Spacer h={4} />
      <AppText fontSize={11} color="#38BDF8">{formatCardDateTime(inquiry.receivedTime)}</AppText>
      <AppText fontSize={11} color={colors.onSurfaceVariant}>Status: {inquiry.status}</AppText>
      {inquiry.status !== 'Replied' ? (
        <>
          <Spacer h={8} />
          <TextInput
            value={reply}
            onChangeText={setReply}
            placeholder="Reply message"
            placeholderTextColor={withAlpha(colors.onSurfaceVariant, 0.8)}
            style={{ borderWidth: 1, borderColor: withAlpha(colors.onSurfaceVariant, 0.25), borderRadius: 10, color: colors.onSurface, paddingHorizontal: 10, paddingVertical: 8 }}
            multiline
          />
          <Spacer h={8} />
          <Pressable onPress={() => onReply(reply)} style={{ alignSelf: 'flex-start' }}>
            <AppText fontSize={12} fontWeight="700" color="#2DD4BF">Send Reply</AppText>
          </Pressable>
        </>
      ) : inquiry.replyMessage ? (
        <>
          <Spacer h={6} />
          <AppText fontSize={11} color="#2DD4BF">Reply: {inquiry.replyMessage}</AppText>
        </>
      ) : null}
    </View>
  );
}

function AppointmentCard({
  appt,
  onConfirm,
  onComplete,
}: {
  appt: Appointment;
  onConfirm: () => void;
  onComplete: () => void;
}) {
  const colors = useColors();
  return (
    <View style={{ borderRadius: 12, backgroundColor: colors.surface, padding: 12, marginBottom: 10 }}>
      <AppText fontSize={14} fontWeight="800">{appt.serviceType}</AppText>
      <AppText fontSize={12} color={colors.onSurfaceVariant}>{appt.clientName} • {appt.clientEmail}</AppText>
      <AppText fontSize={12} color={colors.onSurfaceVariant}>{appt.durationMinutes} minutes</AppText>
      <AppText fontSize={12} color="#38BDF8">{formatCardDateTime(appt.dateTime)}</AppText>
      <AppText fontSize={11} color={colors.onSurfaceVariant}>Status: {appt.status}</AppText>
      <View style={{ flexDirection: 'row', gap: 12, marginTop: 8 }}>
        {appt.status === 'Pending' ? (
          <Pressable onPress={onConfirm}><AppText fontSize={12} fontWeight="700" color="#2DD4BF">Confirm</AppText></Pressable>
        ) : null}
        {appt.status !== 'Completed' ? (
          <Pressable onPress={onComplete}><AppText fontSize={12} fontWeight="700" color="#FB923C">Mark Complete</AppText></Pressable>
        ) : null}
      </View>
    </View>
  );
}

function OutboxItem({ log }: { log: EmailLog }) {
  const colors = useColors();
  return (
    <View style={{ borderRadius: 12, backgroundColor: colors.surface, padding: 12, marginBottom: 10 }}>
      <AppText fontSize={13} fontWeight="700">{log.subject}</AppText>
      <AppText fontSize={11} color={colors.onSurfaceVariant}>To: {log.recipient}</AppText>
      <AppText fontSize={11} color="#38BDF8">{formatOutboxDateTime(log.sentTime)}</AppText>
      <AppText fontSize={11} color={colors.onSurfaceVariant}>Event: {log.triggerEvent} • {log.status}</AppText>
    </View>
  );
}

const SERVICE_OPTIONS = [
  'Data Platform Architecture & Strategy',
  'Cloud & Digital Transformation',
  'Mobile Application Development',
  'Web Application Development',
  'Bespoke Technology Consulting / Other',
] as const;

function isValidEmail(email: string): boolean {
  return /^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/.test(email);
}

async function openExternalUrl(url: string, failureMessage: string): Promise<void> {
  try {
    const supported = await Linking.canOpenURL(url);
    if (!supported) {
      Alert.alert('Unavailable', failureMessage);
      return;
    }
    await Linking.openURL(url);
  } catch {
    Alert.alert('Unavailable', failureMessage);
  }
}

function ContactUsForm() {
  const submitInquiry = useAppStore((s) => s.submitInquiry);
  const colors = useColors();

  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [company, setCompany] = useState('');
  const [selectedService, setSelectedService] = useState<string>(
    'Data Platform Architecture & Strategy',
  );
  const [messageDescription, setMessageDescription] = useState('');
  const [isSubmitted, setIsSubmitted] = useState(false);

  const [nameError, setNameError] = useState<string>('');
  const [emailError, setEmailError] = useState<string>('');
  const [messageError, setMessageError] = useState<string>('');

  if (isSubmitted) {
    return (
      <View style={styles.formCard}>
        <View
          style={{
            width: '100%',
            borderRadius: 12,
            backgroundColor: withAlpha('#10B981', 0.1),
            padding: 16,
            alignItems: 'center',
          }}
        >
          <Icon name="CheckCircle" size={44} color="#10B981" contentDescription="Success" />
          <Spacer h={8} />
          <AppText fontSize={15} fontWeight="700" color="#10B981">
            Inquiry Submitted!
          </AppText>
          <Spacer h={4} />
          <AppText fontSize={11} textAlign="center" lineHeight={15} color={colors.onSurface}>
            Your consulting inquiry has been received. Our principal architect will compile an
            automated strategy draft and send it to your inbox shortly.
          </AppText>
          <Spacer h={14} />
          <Pressable
            onPress={() => {
              setIsSubmitted(false);
              setName('');
              setEmail('');
              setCompany('');
              setMessageDescription('');
            }}
          >
            <AppText fontSize={12} fontWeight="700" color="#14B8A6">
              Send Another Message
            </AppText>
          </Pressable>
        </View>
      </View>
    );
  }

  return (
    <View style={styles.formCard}>
      <AppText fontSize={10} letterSpacing={1} fontWeight="700" color="#14B8A6">
        SEND A MESSAGE
      </AppText>
      <Spacer h={14} />

      <FormInput
        value={name}
        onChangeText={(v) => {
          if (v.length <= 50) {
            setName(v);
            if (v.trim()) setNameError('');
          }
        }}
        placeholder="Your Name *"
      />
      <AppText fontSize={10} color={nameError ? '#EF4444' : colors.onSurfaceVariant}>
        {nameError || `${name.length}/50`}
      </AppText>
      <Spacer h={6} />

      <FormInput
        value={email}
        onChangeText={(v) => {
          if (v.length <= 50) {
            setEmail(v);
            if (v.trim() && isValidEmail(v)) setEmailError('');
          }
        }}
        placeholder="Email Address *"
        keyboardType="email-address"
      />
      <AppText fontSize={10} color={emailError ? '#EF4444' : colors.onSurfaceVariant}>
        {emailError || `${email.length}/50`}
      </AppText>
      <Spacer h={6} />

      <FormInput
        value={company}
        onChangeText={(v) => {
          if (v.length <= 50) setCompany(v);
        }}
        placeholder="Company / Org"
      />
      <AppText fontSize={10} color={colors.onSurfaceVariant}>
        {company.length}/50
      </AppText>
      <Spacer h={8} />

      <AppText fontSize={12} fontWeight="700" color={colors.onSurfaceVariant}>
        Consulting Service Required *
      </AppText>
      <Spacer h={6} />
      <ScrollView horizontal showsHorizontalScrollIndicator={false}>
        <View style={{ flexDirection: 'row' }}>
          {SERVICE_OPTIONS.map((service) => (
            <GlassyChip
              key={service}
              text={service}
              selected={selectedService === service}
              onPress={() => setSelectedService(service)}
            />
          ))}
        </View>
      </ScrollView>

      <FormInput
        value={messageDescription}
        onChangeText={(v) => {
          if (v.length <= 250) {
            setMessageDescription(v);
            if (v.trim()) setMessageError('');
          }
        }}
        placeholder="Message Description *"
        multiline
      />
      <AppText fontSize={10} color={messageError ? '#EF4444' : colors.onSurfaceVariant}>
        {messageError || `${messageDescription.length}/250`}
      </AppText>
      <Spacer h={8} />

      <GlassyButton
        text="Submit Inquiry"
        onPress={() => {
          let hasError = false;

          if (!name.trim()) {
            setNameError('Name is required');
            hasError = true;
          } else if (name.length > 50) {
            setNameError('Name must be max 50 characters');
            hasError = true;
          } else {
            setNameError('');
          }

          if (!email.trim()) {
            setEmailError('Corporate email is required');
            hasError = true;
          } else if (email.length > 50) {
            setEmailError('Email must be max 50 characters');
            hasError = true;
          } else if (!isValidEmail(email)) {
            setEmailError('Invalid email format');
            hasError = true;
          } else {
            setEmailError('');
          }

          if (!messageDescription.trim()) {
            setMessageError('Message is required');
            hasError = true;
          } else if (messageDescription.length > 250) {
            setMessageError('Message must be max 250 characters');
            hasError = true;
          } else {
            setMessageError('');
          }

          if (hasError) {
            Alert.alert('Validation', 'Please complete all required fields correctly');
            return;
          }

          void submitInquiry({
            clientName: name,
            clientEmail: email,
            companyName: company.trim() || 'Independent',
            subject: selectedService,
            message: messageDescription,
          });

          setIsSubmitted(true);
          Alert.alert('Success', 'Inquiry dispatched successfully!');
        }}
      />
    </View>
  );
}

function BookCallForm({
  preselectedService,
  clearPreselected,
}: {
  preselectedService: string;
  clearPreselected: () => void;
}) {
  const createAppointment = useAppStore((s) => s.createAppointment);
  const colors = useColors();

  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [serviceType, setServiceType] = useState(
    preselectedService || 'Data Platform Architecture & Strategy',
  );
  const [proposedDate, setProposedDate] = useState('');
  const [proposedTime, setProposedTime] = useState('');
  const [durationMinutes, setDurationMinutes] = useState('30');
  const [notes, setNotes] = useState('');
  const [bookingConfirmed, setBookingConfirmed] = useState(false);

  const [nameError, setNameError] = useState('');
  const [emailError, setEmailError] = useState('');
  const [dateError, setDateError] = useState('');
  const [timeError, setTimeError] = useState('');

  useEffect(() => {
    if (!preselectedService) return;
    setServiceType(preselectedService);
    clearPreselected();
  }, [preselectedService, clearPreselected]);

  if (bookingConfirmed) {
    return (
      <View style={styles.formCard}>
        <View
          style={{
            borderRadius: 16,
            backgroundColor: withAlpha('#10B981', 0.1),
            padding: 24,
            alignItems: 'center',
          }}
        >
          <Icon name="CheckCircle" size={56} color="#10B981" contentDescription="Success" />
          <Spacer h={16} />
          <AppText fontSize={18} fontWeight="800" color="#10B981">
            Discovery Call Booked!
          </AppText>
          <Spacer h={8} />
          <AppText fontSize={12} textAlign="center" lineHeight={16} color={colors.onSurface}>
            We have scheduled your discovery call regarding {serviceType}. An automated SMTP
            confirmation has been logged with your calendar access details.
          </AppText>
          <Spacer h={16} />
          <GlassyButton
            text="Book Another Session"
            onPress={() => {
              setBookingConfirmed(false);
              setName('');
              setEmail('');
              setNotes('');
              setProposedDate('');
              setProposedTime('');
            }}
          />
        </View>
      </View>
    );
  }

  return (
    <View style={styles.formCard}>
      <AppText fontSize={18} fontWeight="800" color="#2DD4BF">
        Request a Discovery Session
      </AppText>
      <AppText fontSize={11} color={colors.onSurfaceVariant}>
        Fill out this brief to reserve a technical alignment discovery slot on our local
        calendar database.
      </AppText>
      <Spacer h={10} />

      <FormInput
        value={name}
        onChangeText={(v) => {
          if (v.length <= 50) {
            setName(v);
            if (v.trim()) setNameError('');
          }
        }}
        placeholder="Your Name *"
      />
      <AppText fontSize={10} color={nameError ? '#EF4444' : colors.onSurfaceVariant}>
        {nameError || `${name.length}/50`}
      </AppText>
      <Spacer h={6} />

      <FormInput
        value={email}
        onChangeText={(v) => {
          if (v.length <= 50) {
            setEmail(v);
            if (v.trim() && isValidEmail(v)) setEmailError('');
          }
        }}
        placeholder="Corporate Email Address *"
        keyboardType="email-address"
      />
      <AppText fontSize={10} color={emailError ? '#EF4444' : colors.onSurfaceVariant}>
        {emailError || `${email.length}/50`}
      </AppText>
      <Spacer h={8} />

      <AppText fontSize={12} fontWeight="700" color={colors.onSurfaceVariant}>
        Select Consulting Domain
      </AppText>
      <Spacer h={6} />
      <ScrollView horizontal showsHorizontalScrollIndicator={false}>
        <View style={{ flexDirection: 'row' }}>
          {SERVICE_OPTIONS.map((cat) => (
            <GlassyChip
              key={cat}
              text={cat}
              selected={serviceType === cat}
              onPress={() => setServiceType(cat)}
            />
          ))}
        </View>
      </ScrollView>

      <View style={{ flexDirection: 'row', gap: 10 }}>
        <View style={{ flex: 1 }}>
          <FormInput
            value={proposedDate}
            onChangeText={(v) => {
              setProposedDate(v);
              if (v.trim()) setDateError('');
            }}
            placeholder="Date * (e.g. August 12, 2026)"
          />
          <AppText fontSize={10} color={dateError ? '#EF4444' : colors.onSurfaceVariant}>
            {dateError || 'NZST timezone expected'}
          </AppText>
        </View>
        <View style={{ flex: 1 }}>
          <FormInput
            value={proposedTime}
            onChangeText={(v) => {
              setProposedTime(v);
              if (v.trim()) setTimeError('');
            }}
            placeholder="Time * (e.g. 02:30 PM)"
          />
          <AppText fontSize={10} color={timeError ? '#EF4444' : colors.onSurfaceVariant}>
            {timeError || '12-hour format with AM/PM'}
          </AppText>
        </View>
      </View>

      <AppText fontSize={12} fontWeight="700" color={colors.onSurfaceVariant}>
        Session Duration
      </AppText>
      <Spacer h={4} />
      <View style={{ flexDirection: 'row', flexWrap: 'wrap', gap: 8 }}>
        {['15', '30', '45', '60'].map((mins) => (
          <GlassyChip
            key={mins}
            text={`${mins} min`}
            selected={durationMinutes === mins}
            onPress={() => setDurationMinutes(mins)}
          />
        ))}
      </View>

      <FormInput
        value={notes}
        onChangeText={(v) => {
          if (v.length <= 250) setNotes(v);
        }}
        placeholder="Describe your technical stack or goals"
        multiline
      />
      <AppText fontSize={10} color={colors.onSurfaceVariant}>
        {notes.length}/250
      </AppText>
      <Spacer h={10} />

      <GlassyButton
        text="Register Session"
        onPress={() => {
          let hasError = false;

          if (!name.trim()) {
            setNameError('Name is required');
            hasError = true;
          } else if (name.length > 50) {
            setNameError('Name must be max 50 characters');
            hasError = true;
          } else {
            setNameError('');
          }

          if (!email.trim()) {
            setEmailError('Corporate email is required');
            hasError = true;
          } else if (email.length > 50) {
            setEmailError('Email must be max 50 characters');
            hasError = true;
          } else if (!isValidEmail(email)) {
            setEmailError('Invalid email format');
            hasError = true;
          } else {
            setEmailError('');
          }

          if (!proposedDate.trim()) {
            setDateError('Date required');
            hasError = true;
          } else {
            setDateError('');
          }

          if (!proposedTime.trim()) {
            setTimeError('Time required');
            hasError = true;
          } else {
            setTimeError('');
          }

          if (notes.length > 250) {
            hasError = true;
          }

          if (hasError) {
            Alert.alert('Validation', 'Please fill out all mandatory fields correctly');
            return;
          }

          const parsed = Date.parse(`${proposedDate} ${proposedTime}`);
          const timeMs = Number.isNaN(parsed) ? Date.now() + 24 * 3600 * 1000 : parsed;

          if (timeMs < Date.now()) {
            setDateError('Date/time cannot be in the past (NZST)');
            setTimeError('Date/time cannot be in the past (NZST)');
            return;
          }

          void createAppointment({
            clientName: name,
            clientEmail: email,
            serviceType,
            dateTime: timeMs,
            durationMinutes: Number(durationMinutes) || 30,
            notes: `Date: ${proposedDate}, Time: ${proposedTime}. Notes: ${notes}`,
          });
          setBookingConfirmed(true);
        }}
      />
    </View>
  );
}

function ContactUsScreen() {
  const colors = useColors();

  return (
    <ScrollView contentContainerStyle={{ paddingBottom: 26 }}>
      <AppText fontSize={18} fontWeight="800" color={colors.onBackground}>
        Contact Us Directly
      </AppText>
      <AppText fontSize={12} lineHeight={18} color={colors.onSurfaceVariant}>
        Feel free to reach out via email or phone for immediate consulting inquiries. Based in New
        Zealand, available for both localized NZ/AU engagements and remote global advisory roles.
      </AppText>
      <Spacer h={10} />

      <View style={styles.formCard}>
        <AppText fontSize={10} fontWeight="700" letterSpacing={1} color="#14B8A6">
          CONTACT DETAILS
        </AppText>
        <Spacer h={12} />

        <Pressable
          onPress={() => void openExternalUrl('mailto:vaanconsulting@gmail.com', 'Could not open email application')}
        >
          <AppText fontSize={9} fontWeight="700" color={colors.onSurfaceVariant}>
            EMAIL ADDRESS
          </AppText>
          <AppText fontSize={14} fontWeight="700" color="#38BDF8">
            vaanconsulting@gmail.com
          </AppText>
        </Pressable>

        <Spacer h={16} />

        <Pressable onPress={() => void openExternalUrl('tel:+64225601989', 'Could not open dialer')}>
          <AppText fontSize={9} fontWeight="700" color={colors.onSurfaceVariant}>
            PHONE CONTACT
          </AppText>
          <AppText fontSize={14} fontWeight="700" color="#38BDF8">
            +64 22 560 1989
          </AppText>
        </Pressable>

        <Spacer h={16} />

        <Pressable
          onPress={() => void openExternalUrl('https://vaanconsulting.com', 'Could not open browser')}
        >
          <AppText fontSize={9} fontWeight="700" color={colors.onSurfaceVariant}>
            WEBSITE
          </AppText>
          <AppText fontSize={14} fontWeight="700" color="#38BDF8">
            vaanconsulting.com
          </AppText>
        </Pressable>

        <Spacer h={16} />

        <AppText fontSize={9} fontWeight="700" color={colors.onSurfaceVariant}>
          CONSULTING OFFICE
        </AppText>
        <AppText fontSize={14} fontWeight="700" color={colors.onSurface}>
          New Zealand
        </AppText>

        <Spacer h={20} />
        <HorizontalDivider color={withAlpha(colors.onSurface, 0.08)} />
        <Spacer h={12} />
        <AppText fontSize={11} fontWeight="700" color="#2DD4BF">
          VAAN Consulting Limited • New Zealand
        </AppText>
      </View>

      <Spacer h={16} />
      <ContactUsForm />
      <Spacer h={16} />

      <Pressable
        onPress={() => void openExternalUrl('https://wa.me/64225601989', 'Could not open WhatsApp link')}
      >
        <View style={styles.formCard}>
          <View style={{ flexDirection: 'row', alignItems: 'center' }}>
            <View
              style={{
                width: 40,
                height: 40,
                borderRadius: 20,
                backgroundColor: withAlpha('#10B981', 0.1),
                alignItems: 'center',
                justifyContent: 'center',
              }}
            >
              <Icon name="Chat" size={20} color="#10B981" contentDescription="WhatsApp" />
            </View>
            <Spacer w={16} />
            <View style={{ flex: 1 }}>
              <AppText fontSize={14} fontWeight="700">
                Direct WhatsApp
              </AppText>
              <AppText fontSize={12} color="#10B981">
                Chat instantly with Vasanth N on +64 22 560 1989
              </AppText>
            </View>
          </View>
        </View>
      </Pressable>
    </ScrollView>
  );
}

function PortalScreen({
  preselectedService,
  clearPreselected,
  initialTab,
}: {
  preselectedService: string;
  clearPreselected: () => void;
  initialTab: PortalTab;
}) {
  const colors = useColors();
  const [portalTab, setPortalTab] = useState<PortalTab>(initialTab);

  useEffect(() => {
    setPortalTab(initialTab);
  }, [initialTab]);

  return (
    <ScrollView contentContainerStyle={{ padding: 16, paddingBottom: 26 }}>
      <View
        style={{
          width: '100%',
          borderRadius: 12,
          borderWidth: 1,
          borderColor: withAlpha(colors.onSurfaceVariant, 0.22),
          overflow: 'hidden',
          marginBottom: 10,
        }}
      >
        <View style={{ flexDirection: 'row' }}>
          <Pressable
            onPress={() => setPortalTab('book')}
            style={{
              flex: 1,
              paddingVertical: 12,
              alignItems: 'center',
              backgroundColor:
                portalTab === 'book' ? withAlpha('#2DD4BF', 0.14) : withAlpha(colors.background, 0.4),
            }}
          >
            <AppText fontSize={11} fontWeight="700" color={portalTab === 'book' ? '#2DD4BF' : colors.onSurface}>
              Book Call
            </AppText>
          </Pressable>
          <Pressable
            onPress={() => setPortalTab('contact')}
            style={{
              flex: 1,
              paddingVertical: 12,
              alignItems: 'center',
              backgroundColor:
                portalTab === 'contact'
                  ? withAlpha('#2DD4BF', 0.14)
                  : withAlpha(colors.background, 0.4),
            }}
          >
            <AppText
              fontSize={11}
              fontWeight="700"
              color={portalTab === 'contact' ? '#2DD4BF' : colors.onSurface}
            >
              Contact Us
            </AppText>
          </Pressable>
        </View>
      </View>

      <Spacer h={10} />

      {portalTab === 'book' ? (
        <BookCallForm
          preselectedService={preselectedService}
          clearPreselected={clearPreselected}
        />
      ) : null}

      {portalTab === 'contact' ? <ContactUsScreen /> : null}
    </ScrollView>
  );
}

function FormInput({
  value,
  onChangeText,
  placeholder,
  multiline,
  keyboardType,
}: {
  value: string;
  onChangeText: (value: string) => void;
  placeholder: string;
  multiline?: boolean;
  keyboardType?: 'default' | 'number-pad' | 'email-address';
}) {
  const colors = useColors();
  return (
    <TextInput
      value={value}
      onChangeText={onChangeText}
      placeholder={placeholder}
      placeholderTextColor={withAlpha(colors.onSurfaceVariant, 0.8)}
      multiline={multiline}
      keyboardType={keyboardType}
      autoCapitalize="none"
      style={{
        minHeight: multiline ? 72 : 44,
        borderRadius: 10,
        borderWidth: 1,
        borderColor: withAlpha(colors.onSurfaceVariant, 0.25),
        color: colors.onSurface,
        paddingHorizontal: 10,
        paddingVertical: 8,
        marginBottom: 8,
      }}
    />
  );
}

function SmallInput({ value, onChangeText, placeholder }: { value: string; onChangeText: (value: string) => void; placeholder: string }) {
  const colors = useColors();
  return (
    <TextInput
      value={value}
      onChangeText={onChangeText}
      placeholder={placeholder}
      placeholderTextColor={withAlpha(colors.onSurfaceVariant, 0.8)}
      keyboardType="number-pad"
      style={{
        flex: 1,
        minHeight: 44,
        borderRadius: 10,
        borderWidth: 1,
        borderColor: withAlpha(colors.onSurfaceVariant, 0.25),
        color: colors.onSurface,
        paddingHorizontal: 10,
        paddingVertical: 8,
      }}
    />
  );
}

function MainApp() {
  const autoEmailEnabled = useAppStore((s) => s.autoEmailEnabled);
  const meetings = useAppStore((s) => s.meetings);
  const inquiries = useAppStore((s) => s.inquiries);
  const appointments = useAppStore((s) => s.appointments);
  const emailLogs = useAppStore((s) => s.emailLogs);
  const bookmarkedArticles = useAppStore((s) => s.bookmarkedArticles);
  const bookmarkedServices = useAppStore((s) => s.bookmarkedServices);
  const notificationPermissionPrompt = useAppStore((s) => s.notificationPermissionPrompt);
  const dismissNotificationPrompt = useAppStore((s) => s.dismissNotificationPrompt);
  const toggleArticleBookmark = useAppStore((s) => s.toggleArticleBookmark);
  const toggleServiceBookmark = useAppStore((s) => s.toggleServiceBookmark);
  const initialise = useAppStore((s) => s.initialise);
  const isInitialised = useAppStore((s) => s.isInitialised);

  const [showSplash, setShowSplash] = useState(true);
  const [tab, setTab] = useState<RootTab>('home');
  const [portalTab, setPortalTab] = useState<PortalTab>('book');
  const [selectedServiceForBooking, setSelectedServiceForBooking] = useState('');
  const tabOpacity = React.useRef(new Animated.Value(1)).current;
  const tabScale = React.useRef(new Animated.Value(1)).current;

  useEffect(() => {
    void initialise();
  }, [initialise]);

  useEffect(() => {
    const timer = setTimeout(() => setShowSplash(false), 2200);
    return () => clearTimeout(timer);
  }, []);

  useEffect(() => {
    if (!notificationPermissionPrompt) return;
    Alert.alert(
      'Enable Delivery Alerts',
      'To see real-time alerts whenever automated notifications are dispatched to clients, please ensure system notifications are permitted.',
      [{ text: 'Got It', onPress: dismissNotificationPrompt }],
    );
  }, [dismissNotificationPrompt, notificationPermissionPrompt]);

  useEffect(() => {
    tabOpacity.setValue(0);
    tabScale.setValue(0.97);
    Animated.parallel([
      Animated.timing(tabOpacity, {
        toValue: 1,
        duration: 280,
        easing: Easing.out(Easing.cubic),
        useNativeDriver: true,
      }),
      Animated.timing(tabScale, {
        toValue: 1,
        duration: 280,
        easing: Easing.out(Easing.cubic),
        useNativeDriver: true,
      }),
    ]).start();
  }, [tab, tabOpacity, tabScale]);

  const content = useMemo(() => {
    if (tab === 'home') {
      return (
        <HomeScreen
          meetings={meetings}
          inquiries={inquiries}
          appointments={appointments}
          emailLogs={emailLogs}
          onNavigate={(nextTab) => {
            if (nextTab === 'bookings') setPortalTab('book');
            setTab(nextTab);
          }}
          onOpenPortalTab={(nextPortalTab) => setPortalTab(nextPortalTab)}
        />
      );
    }
    if (tab === 'services') {
      return (
        <ServicesScreen
          bookmarked={bookmarkedServices}
          onToggleBookmark={(id) => void toggleServiceBookmark(id)}
          onBookService={(serviceTitle) => {
            setSelectedServiceForBooking(serviceTitle);
            setPortalTab('book');
            setTab('bookings');
          }}
        />
      );
    }
    if (tab === 'insights') {
      return (
        <InsightsScreen
          bookmarked={bookmarkedArticles}
          onToggleBookmark={(id) => void toggleArticleBookmark(id)}
        />
      );
    }
    if (tab === 'chatbot') {
      return <ChatScreen />;
    }
    return (
      <PortalScreen
        preselectedService={selectedServiceForBooking}
        clearPreselected={() => setSelectedServiceForBooking('')}
        initialTab={portalTab}
      />
    );
  }, [
    tab,
    meetings,
    inquiries,
    appointments,
    emailLogs,
    bookmarkedServices,
    bookmarkedArticles,
    toggleServiceBookmark,
    toggleArticleBookmark,
    selectedServiceForBooking,
  ]);

  if (showSplash || !isInitialised) {
    return <Splash onDismiss={() => setShowSplash(false)} />;
  }

  return (
    <SafeAreaView style={{ flex: 1, backgroundColor: '#0F172A' }}>
      <StatusBar barStyle="light-content" backgroundColor="#0F172A" />
      <TopBar autoEmailEnabled={autoEmailEnabled} />
      <Animated.View style={{ flex: 1, opacity: tabOpacity, transform: [{ scale: tabScale }] }}>
        {content}
      </Animated.View>
      <BottomBar tab={tab} onChange={setTab} />
    </SafeAreaView>
  );
}

export default function App() {
  return (
    <VaanThemeProvider dark>
      <MainApp />
    </VaanThemeProvider>
  );
}

const styles = StyleSheet.create({
  topBar: {
    minHeight: 60,
    paddingHorizontal: 16,
    paddingVertical: 10,
    flexDirection: 'row',
    alignItems: 'center',
  },
  splashRoot: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    padding: 24,
  },
  loader: {
    width: 32,
    height: 32,
    borderRadius: 16,
    borderWidth: 3,
    borderColor: '#38BDF8',
    borderTopColor: 'transparent',
  },
  formCard: {
    borderRadius: 14,
    backgroundColor: '#1E293B',
    padding: 12,
    borderWidth: 1,
    borderColor: withAlpha('#FFFFFF', 0.06),
  },
  overlayBackdrop: {
    flex: 1,
    backgroundColor: withAlpha('#020617', 0.78),
    alignItems: 'center',
    justifyContent: 'center',
    padding: 14,
  },
  overlayCard: {
    width: '100%',
    maxWidth: 680,
    maxHeight: '90%',
    borderRadius: 16,
    backgroundColor: '#0B132B',
    padding: 14,
    borderWidth: 1,
    borderColor: withAlpha('#FFFFFF', 0.15),
  },
  dialogAction: {
    borderWidth: 1,
    borderRadius: 10,
    paddingHorizontal: 12,
    paddingVertical: 10,
    alignItems: 'center',
  },
  toggleField: {
    flex: 1,
    minHeight: 44,
    borderRadius: 10,
    borderWidth: 1,
    borderColor: withAlpha('#CAC4D0', 0.25),
    alignItems: 'center',
    justifyContent: 'center',
  },
});
