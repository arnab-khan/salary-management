import { DatePipe, NgClass, NgTemplateOutlet } from '@angular/common';
import { Component, DestroyRef, OnInit, TemplateRef, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectChange, MatSelectModule } from '@angular/material/select';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged, finalize } from 'rxjs/operators';
import { Auth } from '../../../../core/auth/services/auth';
import { CurrencyOptionResponse, Employee, EmployeeFilters, EnumOptionResponse } from '../../services/employee';
import { EmployeeResponse } from '../../../../shared/interfaces/employee.interfaces';
import { SalaryHistoryDialog } from '../../../salary/components/salary-history-dialog/salary-history-dialog';

type SortDirection = 'asc' | 'desc';

@Component({
  selector: 'app-employee-list',
  imports: [DatePipe, NgClass, NgTemplateOutlet, MatDialogModule, MatPaginatorModule, MatProgressSpinnerModule, MatSelectModule],
  templateUrl: './employee-list.html',
  styleUrl: './employee-list.scss',
})
export class EmployeeList implements OnInit {
  private readonly auth = inject(Auth);
  private readonly router = inject(Router);
  private readonly employeeService = inject(Employee);
  private readonly destroyRef = inject(DestroyRef);
  private readonly dialog = inject(MatDialog);
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
  readonly isFilterOpen = signal(false);
  readonly selectedRoles = signal<string[]>([]);
  readonly selectedCountries = signal<string[]>([]);
  readonly selectedCurrency = signal('');
  readonly selectedExperience = signal('');
  readonly roleOptions = signal<EnumOptionResponse[]>([]);
  readonly countryOptions = signal<EnumOptionResponse[]>([]);
  readonly currencyOptions = signal<CurrencyOptionResponse[]>([]);
  readonly roleOptionSearch = signal('');
  readonly countryOptionSearch = signal('');

  ngOnInit(): void {
    this.subscribeToSearchKeywordChanges();
    this.loadFilterOptions();
    this.loadEmployees();
  }

  private subscribeToSearchKeywordChanges(): void {
    this.searchKeywordChange.pipe(
      debounceTime(500),
      distinctUntilChanged(),
      takeUntilDestroyed(this.destroyRef),
    ).subscribe((keyword) => this.searchEmployees(keyword));
  }

  private loadFilterOptions(): void {
    this.employeeService.getEnums().subscribe({
      next: (response) => {
        this.roleOptions.set(response.roles);
        this.countryOptions.set(response.countries);
        this.currencyOptions.set(response.currencies);
      },
      error: () => {
        this.roleOptions.set([]);
        this.countryOptions.set([]);
        this.currencyOptions.set([]);
      },
    });
  }

  loadEmployees(): void {
    this.isLoadingEmployees.set(true);
    this.employeeService.getEmployees(
      this.pageIndex(),
      this.pageSize(),
      `${this.sortField()},${this.sortDirection()}`,
      this.searchKeyword(),
      this.getSelectedFilters(),
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
    if (field === 'currentSalaryAmount' && !this.selectedCurrency()) {
      return;
    }

    if (this.sortField() === field) {
      this.sortDirection.set(this.sortDirection() === 'asc' ? 'desc' : 'asc');
    } else {
      this.sortField.set(field);
      this.sortDirection.set('asc');
    }

    this.pageIndex.set(0);
    this.loadEmployees();
  }

  openCurrencySortDialog(dialogTemplate: TemplateRef<unknown>): void {
    this.dialog.open(dialogTemplate, {
      autoFocus: false,
      maxWidth: 'calc(100vw - 2rem)',
      width: '24rem',
    });
  }

  closeCurrencySortDialog(): void {
    this.dialog.closeAll();
  }

  applyCurrencySort(): void {
    if (!this.selectedCurrency()) {
      return;
    }

    this.closeCurrencySortDialog();
    this.sortBy('currentSalaryAmount');
  }

  searchEmployees(keyword: string): void {
    this.searchKeyword.set(keyword.trim());
    this.pageIndex.set(0);
    this.loadEmployees();
  }

  onSearchKeywordChange(keyword: string): void {
    this.searchKeywordChange.next(keyword);
  }

  openFilter(): void {
    this.isFilterOpen.set(true);
  }

  closeFilter(): void {
    this.isFilterOpen.set(false);
  }

  applyFilters(): void {
    this.pageIndex.set(0);
    this.closeFilter();
    this.loadEmployees();
  }

  clearFilters(): void {
    this.selectedRoles.set([]);
    this.selectedCountries.set([]);
    this.selectedCurrency.set('');
    this.selectedExperience.set('');
    this.applyFilters();
  }

  hasActiveFilters(): boolean {
    return this.selectedRoles().length > 0
      || this.selectedCountries().length > 0
      || this.selectedCurrency() !== ''
      || this.selectedExperience() !== '';
  }

  onRoleFilterChange(event: MatSelectChange): void {
    this.selectedRoles.set(event.value);
  }

  onCountryFilterChange(event: MatSelectChange): void {
    this.selectedCountries.set(event.value);
  }

  onCurrencyFilterChange(event: MatSelectChange): void {
    this.selectedCurrency.set(event.value);
  }

  filteredRoleOptions(): EnumOptionResponse[] {
    return this.filteredOptions(this.roleOptions(), this.roleOptionSearch());
  }

  filteredCountryOptions(): EnumOptionResponse[] {
    return this.filteredOptions(this.countryOptions(), this.countryOptionSearch());
  }

  onRoleOptionSearchChange(keyword: string): void {
    this.roleOptionSearch.set(keyword);
  }

  onCountryOptionSearchChange(keyword: string): void {
    this.countryOptionSearch.set(keyword);
  }

  onExperienceChange(experience: string): void {
    this.selectedExperience.set(experience);
  }

  roleLabel(role: string): string {
    return this.optionLabel(this.roleOptions(), role);
  }

  countryLabel(country: string): string {
    return this.optionLabel(this.countryOptions(), country);
  }

  currencyIcon(currency: string): string {
    return this.currencyOptions().find((option) => option.value === currency)?.icon ?? currency;
  }

  openSalaryHistory(employee: EmployeeResponse): void {
    this.dialog.open(SalaryHistoryDialog, {
      autoFocus: false,
      data: {
        currencyIcon: (currency: string) => this.currencyIcon(currency),
        employeeId: employee.id,
        employeeName: employee.name,
      },
      height: '90dvh',
      maxWidth: '90dvw',
      width: '36rem',
    });
  }

  private getSelectedFilters(): EmployeeFilters {
    return {
      countries: this.selectedCountries(),
      currency: this.selectedCurrency(),
      experience: this.selectedExperience(),
      roles: this.selectedRoles(),
    };
  }

  private filteredOptions(options: EnumOptionResponse[], keyword: string): EnumOptionResponse[] {
    const normalizedKeyword = keyword.trim().toLowerCase();

    if (!normalizedKeyword) {
      return options;
    }

    return options.filter((option) => option.label.toLowerCase().includes(normalizedKeyword));
  }

  private optionLabel(options: EnumOptionResponse[], value: string): string {
    return options.find((option) => option.value === value)?.label ?? value;
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
