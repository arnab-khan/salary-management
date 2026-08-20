import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { of } from 'rxjs';

import { EmployeeList } from './employee-list';
import { Auth } from '../../../../core/auth/services/auth';
import { Employee } from '../../services/employee';

describe('EmployeeList', () => {
  let component: EmployeeList;
  let fixture: ComponentFixture<EmployeeList>;
  let employeeService: Pick<Employee, 'getEmployees' | 'getEnums'>;

  beforeEach(async () => {
    employeeService = {
      getEmployees: vi.fn().mockImplementation((page: number, size: number) => of({
        content: [],
        number: page,
        size,
        totalElements: 0,
        totalPages: 0,
      })),
      getEnums: vi.fn().mockReturnValue(of({ countries: [], currencies: [], roles: [] })),
    };

    await TestBed.configureTestingModule({
      imports: [EmployeeList],
      providers: [
        { provide: Employee, useValue: employeeService },
        { provide: Auth, useValue: { logout: vi.fn() } },
        { provide: Router, useValue: { navigateByUrl: vi.fn() } },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(EmployeeList);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should reset to first page when page size changes', () => {
    component.pageIndex.set(3);
    component.pageSize.set(50);

    component.onPageChange({ pageIndex: 3, pageSize: 100, length: 1000 });

    expect(component.pageIndex()).toBe(0);
    expect(component.pageSize()).toBe(100);
    expect(employeeService.getEmployees).toHaveBeenCalledWith(0, 100, 'joiningDate,desc', '', {
      countries: [],
      currency: '',
      experience: '',
      roles: [],
    });
  });

  it('should keep selected page when page size does not change', () => {
    component.pageIndex.set(0);
    component.pageSize.set(50);

    component.onPageChange({ pageIndex: 2, pageSize: 50, length: 1000 });

    expect(component.pageIndex()).toBe(2);
    expect(component.pageSize()).toBe(50);
    expect(employeeService.getEmployees).toHaveBeenCalledWith(2, 50, 'joiningDate,desc', '', {
      countries: [],
      currency: '',
      experience: '',
      roles: [],
    });
  });

  it('should reset current salary sort when filters are applied without currency', () => {
    component.sortField.set('currentSalaryAmount');
    component.sortDirection.set('asc');
    component.selectedCurrency.set('');

    component.applyFilters();

    expect(component.sortField()).toBe('joiningDate');
    expect(component.sortDirection()).toBe('desc');
    expect(employeeService.getEmployees).toHaveBeenCalledWith(0, 50, 'joiningDate,desc', '', {
      countries: [],
      currency: '',
      experience: '',
      roles: [],
    });
  });

  it('should keep current salary sort when filters are applied with currency', () => {
    component.sortField.set('currentSalaryAmount');
    component.sortDirection.set('asc');
    component.selectedCurrency.set('INR');

    component.applyFilters();

    expect(component.sortField()).toBe('currentSalaryAmount');
    expect(component.sortDirection()).toBe('asc');
    expect(employeeService.getEmployees).toHaveBeenCalledWith(0, 50, 'currentSalaryAmount,asc', '', {
      countries: [],
      currency: 'INR',
      experience: '',
      roles: [],
    });
  });
});
