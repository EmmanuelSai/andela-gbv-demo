import { DatePipe } from '@angular/common';
import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../core/api.service';
import { AuditLog, PageResponse } from '../core/models';

@Component({
  selector: 'app-audit',
  imports: [FormsModule, DatePipe],
  template: `
    <header class="page-head">
      <div>
        <p class="eyebrow">Administrator</p>
        <h1>Audit log</h1>
      </div>
    </header>

    <form class="filters" (ngSubmit)="load(0)">
      <label>
        Case ID
        <input name="caseId" [(ngModel)]="caseId" placeholder="optional UUID" />
      </label>
      <button type="submit">Search</button>
    </form>

    @if (error()) {
      <p class="error">{{ error() }}</p>
    }

    <section class="panel">
      <table>
        <thead>
          <tr>
            <th>When</th>
            <th>Actor</th>
            <th>Action</th>
            <th>Case</th>
            <th>Detail</th>
          </tr>
        </thead>
        <tbody>
          @for (row of page()?.content ?? []; track row.id) {
            <tr>
              <td>{{ row.at | date:'medium' }}</td>
              <td>{{ row.actor }}</td>
              <td>{{ row.action }}</td>
              <td>{{ row.caseId || '—' }}</td>
              <td>{{ row.detail || '—' }}</td>
            </tr>
          }
        </tbody>
      </table>
      <div class="pager">
        <button type="button" [disabled]="(page()?.page ?? 0) === 0" (click)="load((page()?.page ?? 1) - 1)">Previous</button>
        <span>Page {{ (page()?.page ?? 0) + 1 }} of {{ page()?.totalPages || 1 }}</span>
        <button type="button" [disabled]="((page()?.page ?? 0) + 1) >= (page()?.totalPages || 1)" (click)="load((page()?.page ?? 0) + 1)">Next</button>
      </div>
    </section>
  `
})
export class AuditComponent implements OnInit {
  caseId = '';
  readonly page = signal<PageResponse<AuditLog> | null>(null);
  readonly error = signal('');

  constructor(private readonly api: ApiService) {}

  ngOnInit(): void {
    this.load(0);
  }

  load(pageIndex: number): void {
    this.api.audit(pageIndex, this.caseId.trim() || undefined).subscribe({
      next: (result) => this.page.set(result),
      error: () => this.error.set('Could not load audit events.')
    });
  }
}
