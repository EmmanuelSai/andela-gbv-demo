import { DatePipe } from '@angular/common';
import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { ApiService } from '../core/api.service';
import { CaseDetail, CaseStatus, DirectoryReferral, ReferralStatus, VaultEntry } from '../core/models';

@Component({
  selector: 'app-case-detail',
  imports: [FormsModule, DatePipe],
  template: `
    @if (error()) {
      <p class="error">{{ error() }}</p>
    }
    @if (detail(); as c) {
      <header class="page-head">
        <div>
          <p class="eyebrow">{{ c.type }} · {{ c.id }}</p>
          <h1>Case file</h1>
        </div>
        <div class="head-meta">
          <span class="pill" [class]="c.status">{{ c.status }}</span>
          <span class="pill" [class]="c.femicideRisk">{{ c.femicideRisk }}</span>
        </div>
      </header>

      <section class="split">
        <article class="panel">
          <h2>Narrative</h2>
          @for (content of c.contents; track content.id) {
            <p class="narrative">{{ content.text }}</p>
            <small class="muted">Language: {{ content.language }}</small>
          }
          @if (!c.contents.length) {
            <p class="muted">No narrative recorded.</p>
          }

          <h2>Risk indicators</h2>
          <ul class="flags">
            @for (flag of flags(c); track flag.label) {
              <li [class.on]="flag.on">{{ flag.label }}</li>
            }
          </ul>
        </article>

        <article class="panel">
          <h2>Workflow</h2>
          <label>
            Status
            <select [(ngModel)]="status">
              @for (option of statuses; track option) {
                <option [value]="option">{{ option }}</option>
              }
            </select>
          </label>
          <button type="button" (click)="saveStatus()">Update status</button>

          <label>
            Assign to
            <input [(ngModel)]="assignedTo" placeholder="staff username" />
          </label>
          <button type="button" (click)="saveAssignment()">Assign</button>
          <p class="muted">Currently: {{ c.assignedTo || 'unassigned' }}</p>
          @if (notice()) {
            <p class="ok">{{ notice() }}</p>
          }
        </article>
      </section>

      <section class="split">
        <article class="panel">
          <h2>Witness contacts</h2>
          @for (contact of c.witnessContacts; track contact.id) {
            <p>{{ contact.safeContact }} <small>{{ contact.createdAt | date:'short' }}</small></p>
          }
          @if (!c.witnessContacts.length) {
            <p class="muted">None recorded.</p>
          }

          <h2>Media</h2>
          @for (item of c.media; track item.id) {
            <p>
              {{ item.contentType }} · {{ item.sizeBytes }} bytes
              @if (item.originalHadGps) { <span class="pill warn">GPS stripped</span> }
            </p>
          }
          @if (!c.media.length) {
            <p class="muted">No media attached.</p>
          }

          <h2>Safety vault ({{ c.vaultCount }})</h2>
          <button type="button" (click)="openVault()">Open vault</button>
          @for (entry of vault(); track entry.id) {
            <pre class="narrative">{{ entry.blob }}</pre>
          }
        </article>

        <article class="panel">
          <h2>Referrals</h2>
          @for (referral of c.referrals; track referral.id) {
            <div class="row">
              <div>
                <strong>{{ referral.serviceType }}</strong>
                <small>{{ referral.serviceId }}</small>
              </div>
              <select [ngModel]="referral.status" (ngModelChange)="changeReferral(referral.id, $event)">
                @for (option of referralStatuses; track option) {
                  <option [value]="option">{{ option }}</option>
                }
              </select>
            </div>
          }
          @if (!c.referrals.length) {
            <p class="muted">No referrals yet.</p>
          }

          <h2>Add referral</h2>
          <label>
            Directory location
            <select [(ngModel)]="location" (ngModelChange)="loadDirectory()">
              @for (loc of locations(); track loc) {
                <option [value]="loc">{{ loc }}</option>
              }
            </select>
          </label>
          <label>
            Service type
            <input [(ngModel)]="serviceType" placeholder="counselling" />
          </label>
          <label>
            Service id
            <input [(ngModel)]="serviceId" placeholder="name or phone" />
          </label>
          <button type="button" (click)="addReferral()">Save referral</button>
          <ul class="stat-list">
            @for (item of directory(); track item.name) {
              <li>
                <span>{{ item.name }} · {{ item.phone }}</span>
                <button type="button" class="linkish" (click)="useDirectory(item)">Use</button>
              </li>
            }
          </ul>
        </article>
      </section>
    }
  `
})
export class CaseDetailComponent implements OnInit {
  readonly statuses: CaseStatus[] = ['NEW', 'UNDER_REVIEW', 'REFERRED', 'ESCALATED', 'CLOSED'];
  readonly referralStatuses: ReferralStatus[] = ['PENDING', 'CONTACTED', 'COMPLETED', 'CANCELLED'];
  readonly detail = signal<CaseDetail | null>(null);
  readonly vault = signal<VaultEntry[]>([]);
  readonly directory = signal<DirectoryReferral[]>([]);
  readonly locations = signal<string[]>([]);
  readonly error = signal('');
  readonly notice = signal('');
  status: CaseStatus = 'NEW';
  assignedTo = '';
  location = 'OTHER';
  serviceType = '';
  serviceId = '';
  private id = '';

  constructor(
    private readonly api: ApiService,
    private readonly route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.id = this.route.snapshot.paramMap.get('id') ?? '';
    this.refresh();
    this.api.locations().subscribe((locs) => this.locations.set(locs));
    this.loadDirectory();
  }

  refresh(): void {
    this.api.getCase(this.id).subscribe({
      next: (c) => {
        this.detail.set(c);
        this.status = c.status;
        this.assignedTo = c.assignedTo ?? '';
      },
      error: () => this.error.set('Could not load this case.')
    });
  }

  saveStatus(): void {
    this.api.updateStatus(this.id, this.status).subscribe({
      next: () => {
        this.notice.set('Status updated.');
        this.refresh();
      },
      error: () => this.error.set('Could not update status.')
    });
  }

  saveAssignment(): void {
    if (!this.assignedTo.trim()) {
      return;
    }
    this.api.assign(this.id, this.assignedTo.trim()).subscribe({
      next: () => {
        this.notice.set('Case assigned.');
        this.refresh();
      },
      error: () => this.error.set('Could not assign this case.')
    });
  }

  openVault(): void {
    this.api.vault(this.id).subscribe({
      next: (entries) => this.vault.set(entries),
      error: () => this.error.set('Vault access failed.')
    });
  }

  addReferral(): void {
    if (!this.serviceType.trim() || !this.serviceId.trim()) {
      return;
    }
    this.api.createReferral(this.id, this.serviceType.trim(), this.serviceId.trim()).subscribe({
      next: () => {
        this.serviceType = '';
        this.serviceId = '';
        this.refresh();
      },
      error: () => this.error.set('Could not create referral.')
    });
  }

  changeReferral(referralId: string, status: ReferralStatus): void {
    this.api.updateReferralStatus(referralId, status).subscribe({
      next: () => this.refresh(),
      error: () => this.error.set('Could not update referral.')
    });
  }

  loadDirectory(): void {
    this.api.directory(this.location).subscribe({
      next: (items) => this.directory.set(items)
    });
  }

  useDirectory(item: DirectoryReferral): void {
    this.serviceType = item.type;
    this.serviceId = `${item.name} (${item.phone})`;
  }

  flags(c: CaseDetail): { label: string; on: boolean }[] {
    const i = c.riskIndicators;
    return [
      { label: 'Strangulation', on: i.strangulation },
      { label: 'Threats to kill', on: i.threatsToKill },
      { label: 'Weapon use', on: i.weaponUse },
      { label: 'Stalking', on: i.stalkingOrMonitoring },
      { label: 'Recent separation', on: i.recentSeparation },
      { label: 'Pregnancy', on: i.pregnancy },
      { label: 'Prior GBV', on: i.priorGBV },
      { label: 'Threats to family', on: i.threatsToFamily },
      { label: 'Substance abuse', on: i.substanceAbuseByPerpetrator },
      { label: 'Access to firearms', on: i.accessToFirearms }
    ];
  }
}
