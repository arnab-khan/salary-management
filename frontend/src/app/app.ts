import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { EMPTY, catchError } from 'rxjs';
import { Auth } from './core/auth/services/auth';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App implements OnInit {
  private readonly auth = inject(Auth);

  protected readonly title = signal('salary-management');

  ngOnInit(): void {
    this.auth.me().pipe(
      catchError(() => EMPTY),
    ).subscribe();
  }
}
