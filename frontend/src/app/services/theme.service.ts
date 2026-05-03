import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ThemeService {
  private darkMode = new BehaviorSubject<boolean>(
    localStorage.getItem('darkMode') === 'true'
  );
  darkMode$ = this.darkMode.asObservable();

  constructor() {
    this.applyTheme(this.darkMode.value);
  }

  toggleDarkMode(): void {
    const next = !this.darkMode.value;
    this.darkMode.next(next);
    localStorage.setItem('darkMode', String(next));
    this.applyTheme(next);
  }

  get isDark(): boolean {
    return this.darkMode.value;
  }

  private applyTheme(dark: boolean): void {
    if (dark) {
      document.body.classList.add('dark-mode');
    } else {
      document.body.classList.remove('dark-mode');
    }
  }
}
