import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { catchError, finalize, tap } from 'rxjs/operators';
import { environment } from '../../../../environments/environment';
import { AuthResponse, LoginRequest } from '../../../shared/interfaces/auth.interfaces';

@Injectable({
  providedIn: 'root',
})
export class Auth {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiBaseUrl}/auth`;
  private readonly currentUserSubject = new BehaviorSubject<AuthResponse | null>(null);
  private readonly sessionCheckedSubject = new BehaviorSubject(false);

  readonly currentUser$ = this.currentUserSubject.asObservable();
  readonly sessionChecked$ = this.sessionCheckedSubject.asObservable();

  login(loginRequest: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, loginRequest).pipe(
      tap((authResponse) => {
        this.currentUserSubject.next(authResponse);
        this.sessionCheckedSubject.next(true);
      }),
      catchError((error) => {
        this.currentUserSubject.next(null);
        this.sessionCheckedSubject.next(true);
        return throwError(() => error);
      }),
    );
  }

  me(): Observable<AuthResponse> {
    return this.http.get<AuthResponse>(`${this.apiUrl}/me`).pipe(
      tap((authResponse) => this.currentUserSubject.next(authResponse)),
      catchError((error) => {
        this.currentUserSubject.next(null);
        return throwError(() => error);
      }),
      finalize(() => this.sessionCheckedSubject.next(true)),
    );
  }

  logout(): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/logout`, null).pipe(
      tap(() => {
        this.currentUserSubject.next(null);
        this.sessionCheckedSubject.next(true);
      }),
    );
  }
}
