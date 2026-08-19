import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { EmployeeResponse, PageResponse } from '../../../shared/interfaces/employee.interfaces';

export interface EmployeeFilters {
  countries: string[];
  currency: string;
  experience: string;
  roles: string[];
}

export interface EnumOptionResponse {
  label: string;
  value: string;
}

export interface CurrencyOptionResponse extends EnumOptionResponse {
  icon: string;
}

export interface EnumsResponse {
  countries: EnumOptionResponse[];
  currencies: CurrencyOptionResponse[];
  roles: EnumOptionResponse[];
}

@Injectable({
  providedIn: 'root',
})
export class Employee {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiBaseUrl}/employees`;
  private readonly enumsUrl = `${environment.apiBaseUrl}/enums`;

  getEmployees(
    page = 0,
    size = 10,
    sort = 'name,asc',
    keyword = '',
    filters?: EmployeeFilters,
  ): Observable<PageResponse<EmployeeResponse>> {
    let params = new HttpParams()
      .set('page', page)
      .set('size', size)
      .set('sort', sort);

    if (keyword.trim()) {
      params = params.set('keyword', keyword.trim());
    }

    if (filters?.roles.length) {
      params = params.set('roles', filters.roles.join(','));
    }

    if (filters?.countries.length) {
      params = params.set('countries', filters.countries.join(','));
    }

    if (filters?.currency) {
      params = params.set('currency', filters.currency);
    }

    if (filters?.experience) {
      params = params.set('experience', filters.experience);
    }

    return this.http.get<PageResponse<EmployeeResponse>>(this.apiUrl, { params });
  }

  getEnums(): Observable<EnumsResponse> {
    return this.http.get<EnumsResponse>(this.enumsUrl);
  }
}
