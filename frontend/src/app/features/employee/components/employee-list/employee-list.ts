import { NgClass, NgTemplateOutlet } from '@angular/common';
import { Component, DestroyRef, OnInit, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged, finalize } from 'rxjs/operators';
import { Auth } from '../../../../core/auth/services/auth';
import { Employee } from '../../../services/employee';
import { EmployeeResponse } from '../../../../shared/interfaces/employee.interfaces';

type SortDirection = 'asc' | 'desc';

@Component({
  selector: 'app-employee-list',
  imports: [NgClass, NgTemplateOutlet, MatPaginatorModule, MatProgressSpinnerModule],
  templateUrl: './employee-list.html',
  styleUrl: './employee-list.scss',
})
export class EmployeeList implements OnInit {
  private readonly auth = inject(Auth);
  private readonly router = inject(Router);
  private readonly employeeService = inject(Employee);
  private readonly destroyRef = inject(DestroyRef);
  private readonly searchKeywordChange = new Subject<string>();

  readonly isLoggingOut = signal(false);
  readonly isInitialLoading = signal(true);
  readonly isLoadingEmployees = signal(false);
  readonly employees = signal<EmployeeResponse[]>([]);
  readonly totalElements = signal(0);
  readonly pageIndex = signal(0);
  readonly pageSize = signal(50);
  readonly sortField = signal('joiningDate');
  readonly sortDirection = signal<SortDirection>('desc');
  readonly searchKeyword = signal('');

  ngOnInit(): void {
    this.subscribeToSearchKeywordChanges();
    this.loadEmployees();
  }

  private subscribeToSearchKeywordChanges(): void {
    this.searchKeywordChange.pipe(
      debounceTime(500),
      distinctUntilChanged(),
      takeUntilDestroyed(this.destroyRef),
    ).subscribe((keyword) => this.searchEmployees(keyword));
  }

  loadEmployees(): void {
    this.isLoadingEmployees.set(true);
    this.employeeService.getEmployees(
      this.pageIndex(),
      this.pageSize(),
      `${this.sortField()},${this.sortDirection()}`,
      this.searchKeyword(),
    ).pipe(
      finalize(() => {
        this.isInitialLoading.set(false);
        this.isLoadingEmployees.set(false);
      }),
    ).subscribe({
      next: (response) => {
        this.employees.set(response.content);
        this.totalElements.set(response.totalElements);
        this.pageIndex.set(response.number);
        this.pageSize.set(response.size);
      },
      error: () => {
        this.employees.set([]);
        this.totalElements.set(0);
      },
    });
  }

  onPageChange(event: PageEvent): void {
    this.pageIndex.set(event.pageIndex);
    this.pageSize.set(event.pageSize);
    this.loadEmployees();
  }

  sortBy(field: string): void {
    if (this.sortField() === field) {
      this.sortDirection.set(this.sortDirection() === 'asc' ? 'desc' : 'asc');
    } else {
      this.sortField.set(field);
      this.sortDirection.set('asc');
    }

    this.pageIndex.set(0);
    this.loadEmployees();
  }

  searchEmployees(keyword: string): void {
    this.searchKeyword.set(keyword.trim());
    this.pageIndex.set(0);
    this.loadEmployees();
  }

  onSearchKeywordChange(keyword: string): void {
    this.searchKeywordChange.next(keyword);
  }

  logout(): void {
    if (this.isLoggingOut()) {
      return;
    }

    this.isLoggingOut.set(true);

    this.auth.logout().pipe(
      finalize(() => this.isLoggingOut.set(false)),
    ).subscribe({
      next: () => this.router.navigateByUrl('/login'),
      error: () => this.router.navigateByUrl('/login'),
    });
  }
}
