import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { EmployeeResponse, PageResponse } from '../../shared/interfaces/employee.interfaces';

@Injectable({
  providedIn: 'root',
})
export class Employee {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiBaseUrl}/employees`;

  getEmployees(page = 0, size = 10): Observable<PageResponse<EmployeeResponse>> {
    const params = new HttpParams()
      .set('page', page)
      .set('size', size)
      .set('sort', 'name,asc');

    return this.http.get<PageResponse<EmployeeResponse>>(this.apiUrl, { params });
  }
}
