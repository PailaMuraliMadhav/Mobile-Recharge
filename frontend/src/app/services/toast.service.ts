import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

export type ToastType = 'success' | 'error' | 'info' | 'warning';

export interface Toast {
  id: number;
  message: string;
  type: ToastType;
}

@Injectable({ providedIn: 'root' })
export class ToastService {
  private counter = 0;
  private toastsSubject = new Subject<Toast>();
  toasts$ = this.toastsSubject.asObservable();

  show(message: string, type: ToastType = 'info'): void {
    this.toastsSubject.next({ id: ++this.counter, message, type });
  }

  success(message: string): void { this.show(message, 'success'); }
  error(message: string):   void { this.show(message, 'error'); }
  warning(message: string): void { this.show(message, 'warning'); }
  info(message: string):    void { this.show(message, 'info'); }
}
