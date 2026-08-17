import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { environment } from '../../../../environments/environment';
import { AuthResponse, LoginRequest } from '../../../shared/interfaces/auth.interfaces';

@Injectable({
  providedIn: 'root',
})
export class Auth {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiBaseUrl}/auth`;
  private readonly currentUserSubject = new BehaviorSubject<AuthResponse | null>(null);

  readonly currentUser$ = this.currentUserSubject.asObservable();

  login(loginRequest: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, loginRequest).pipe(
      tap((authResponse) => this.currentUserSubject.next(authResponse)),
      catchError((error) => {
        this.currentUserSubject.next({ username: '', authenticated: false });
        return throwError(() => error);
      }),
    );
  }

  me(): Observable<AuthResponse> {
    return this.http.get<AuthResponse>(`${this.apiUrl}/me`).pipe(
      tap((authResponse) => this.currentUserSubject.next(authResponse)),
      catchError((error) => {
        this.currentUserSubject.next({ username: '', authenticated: false });
        return throwError(() => error);
      }),
    );
  }

  logout(): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/logout`, null).pipe(
      tap(() => this.currentUserSubject.next({ username: '', authenticated: false })),
    );
  }
}
