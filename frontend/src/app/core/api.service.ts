import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {
  AuditLog,
  CaseDetail,
  CaseListItem,
  CaseStatus,
  CaseType,
  DashboardSummary,
  DirectoryReferral,
  FemicideRiskLevel,
  LoginResponse,
  PageResponse,
  Referral,
  ReferralStatus,
  StaffRole,
  StaffUser,
  VaultEntry
} from './models';

@Injectable({ providedIn: 'root' })
export class ApiService {
  constructor(private readonly http: HttpClient) {}

  login(username: string, password: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>('/api/auth/login', { username, password });
  }

  me(): Observable<LoginResponse> {
    return this.http.get<LoginResponse>('/api/auth/me');
  }

  dashboard(): Observable<DashboardSummary> {
    return this.http.get<DashboardSummary>('/api/portal/dashboard/summary');
  }

  listCases(filters: {
    status?: CaseStatus | '';
    type?: CaseType | '';
    risk?: FemicideRiskLevel | '';
    assignedTo?: string;
    page?: number;
    size?: number;
  }): Observable<PageResponse<CaseListItem>> {
    let params = new HttpParams()
      .set('page', String(filters.page ?? 0))
      .set('size', String(filters.size ?? 20));
    if (filters.status) params = params.set('status', filters.status);
    if (filters.type) params = params.set('type', filters.type);
    if (filters.risk) params = params.set('risk', filters.risk);
    if (filters.assignedTo) params = params.set('assignedTo', filters.assignedTo);
    return this.http.get<PageResponse<CaseListItem>>('/api/portal/cases', { params });
  }

  getCase(id: string): Observable<CaseDetail> {
    return this.http.get<CaseDetail>(`/api/portal/cases/${id}`);
  }

  updateStatus(id: string, status: CaseStatus): Observable<CaseListItem> {
    return this.http.patch<CaseListItem>(`/api/portal/cases/${id}/status`, { status });
  }

  assign(id: string, assignedTo: string): Observable<CaseListItem> {
    return this.http.patch<CaseListItem>(`/api/portal/cases/${id}/assign`, { assignedTo });
  }

  vault(id: string): Observable<VaultEntry[]> {
    return this.http.get<VaultEntry[]>(`/api/portal/cases/${id}/vault`);
  }

  createReferral(caseId: string, serviceType: string, serviceId: string): Observable<Referral> {
    return this.http.post<Referral>(`/api/portal/cases/${caseId}/referrals`, { serviceType, serviceId });
  }

  updateReferralStatus(id: string, status: ReferralStatus): Observable<Referral> {
    return this.http.patch<Referral>(`/api/portal/referrals/${id}/status`, { status });
  }

  directory(location?: string): Observable<DirectoryReferral[]> {
    let params = new HttpParams();
    if (location) params = params.set('location', location);
    return this.http.get<DirectoryReferral[]>('/api/portal/referrals/directory', { params });
  }

  locations(): Observable<string[]> {
    return this.http.get<string[]>('/api/portal/referrals/locations');
  }

  audit(page = 0, caseId?: string): Observable<PageResponse<AuditLog>> {
    let params = new HttpParams().set('page', String(page)).set('size', '25');
    if (caseId) params = params.set('caseId', caseId);
    return this.http.get<PageResponse<AuditLog>>('/api/portal/audit', { params });
  }

  users(): Observable<StaffUser[]> {
    return this.http.get<StaffUser[]>('/api/portal/users');
  }

  createUser(payload: {
    username: string;
    password: string;
    displayName: string;
    role: StaffRole;
  }): Observable<StaffUser> {
    return this.http.post<StaffUser>('/api/portal/users', payload);
  }

  setUserEnabled(id: string, enabled: boolean): Observable<StaffUser> {
    return this.http.patch<StaffUser>(`/api/portal/users/${id}/enabled`, { enabled });
  }
}
