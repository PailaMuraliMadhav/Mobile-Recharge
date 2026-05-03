import { Component, OnInit, OnDestroy } from '@angular/core';
import { ToastService, Toast } from '../../services/toast.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-toast',
  templateUrl: './toast.component.html',
  styleUrls: ['./toast.component.css']
})
export class ToastComponent implements OnInit, OnDestroy {
  toasts: (Toast & { visible: boolean })[] = [];
  private sub!: Subscription;

  constructor(private toastService: ToastService) {}

  ngOnInit(): void {
    this.sub = this.toastService.toasts$.subscribe(toast => {
      const t = { ...toast, visible: true };
      this.toasts.push(t);
      // Auto-dismiss after 3.5s
      setTimeout(() => this.dismiss(t.id), 3500);
    });
  }

  ngOnDestroy(): void { this.sub?.unsubscribe(); }

  dismiss(id: number): void {
    const t = this.toasts.find(x => x.id === id);
    if (t) {
      t.visible = false;
      setTimeout(() => this.toasts = this.toasts.filter(x => x.id !== id), 350);
    }
  }

  getIcon(type: string): string {
    switch (type) {
      case 'success': return '✅';
      case 'error':   return '❌';
      case 'warning': return '⚠️';
      default:        return 'ℹ️';
    }
  }
}
