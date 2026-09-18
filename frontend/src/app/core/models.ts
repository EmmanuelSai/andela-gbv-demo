export type StaffRole = 'ADMIN' | 'CASE_MANAGER';
export type CaseType = 'SURVIVOR' | 'WITNESS';
export type CaseStatus = 'NEW' | 'UNDER_REVIEW' | 'REFERRED' | 'ESCALATED' | 'CLOSED';
export type FemicideRiskLevel = 'UNKNOWN' | 'LOW' | 'ELEVATED' | 'IMMINENT';
export type ReferralStatus = 'PENDING' | 'CONTACTED' | 'COMPLETED' | 'CANCELLED';

export interface LoginResponse {
  token: string | null;
  role: StaffRole;
  displayName: string;
  username: string;
}

export interface CaseListItem {
  id: string;
  type: CaseType;
  status: CaseStatus;
  femicideRisk: FemicideRiskLevel;
  assignedTo: string | null;
  createdAt: string;
  updatedAt: string | null;
}

export interface FemicideIndicators {
  strangulation: boolean;
  threatsToKill: boolean;
  weaponUse: boolean;
  stalkingOrMonitoring: boolean;
  recentSeparation: boolean;
  pregnancy: boolean;
  priorGBV: boolean;
  threatsToFamily: boolean;
  substanceAbuseByPerpetrator: boolean;
  accessToFirearms: boolean;
  lastUpdated: string;
}

export interface CaseContent {
  id: string;
  text: string;
  language: string;
}

export interface WitnessContact {
  id: string;
  safeContact: string;
  createdAt: string;
}

export interface Referral {
  id: string;
  caseId: string;
  serviceType: string;
  serviceId: string;
  status: ReferralStatus;
  createdAt: string;
}

export interface MediaMetadata {
  id: string;
  contentType: string;
  sha256: string;
  sizeBytes: number;
  createdAt: string;
  originalHadGps: boolean;
  originalHadExif: boolean;
  originalHadXmp: boolean;
}

export interface CaseDetail extends CaseListItem {
  riskIndicators: FemicideIndicators;
  contents: CaseContent[];
  witnessContacts: WitnessContact[];
  referrals: Referral[];
  media: MediaMetadata[];
  vaultCount: number;
}

export interface VaultEntry {
  id: string;
  blob: string;
  createdAt: string;
}

export interface DashboardSummary {
  totalCases: number;
  byStatus: Record<CaseStatus, number>;
  byType: Record<CaseType, number>;
  byRisk: Record<FemicideRiskLevel, number>;
  imminentCount: number;
  escalatedCount: number;
  priorityQueue: CaseListItem[];
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface AuditLog {
  id: string;
  actor: string;
  action: string;
  caseId: string | null;
  detail: string | null;
  at: string;
}

export interface StaffUser {
  id: string;
  username: string;
  displayName: string;
  role: StaffRole;
  enabled: boolean;
  createdAt: string;
}

export interface DirectoryReferral {
  name: string;
  phone: string;
  type: string;
}
