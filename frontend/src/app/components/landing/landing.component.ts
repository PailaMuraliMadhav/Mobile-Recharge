import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-landing',
  templateUrl: './landing.component.html',
  styleUrls: ['./landing.component.css']
})
export class LandingComponent implements OnInit {
  features = [
    { icon: '⚡', title: 'Instant Recharge', desc: 'Recharge any mobile number in seconds with real-time processing.' },
    { icon: '🔒', title: 'Secure Payments', desc: 'Bank-grade security with multiple payment modes supported.' },
    { icon: '📊', title: 'Track History', desc: 'Full transaction history with detailed status tracking.' },
    { icon: '🌐', title: 'All Operators', desc: 'Support for all major telecom operators across India.' },
    { icon: '💬', title: 'Notifications', desc: 'Instant email notifications for every recharge transaction.' },
    { icon: '🎯', title: 'Best Plans', desc: 'Browse and compare the best plans from all operators.' }
  ];

  stats = [
    { value: '10M+', label: 'Recharges Done' },
    { value: '50+', label: 'Operators' },
    { value: '99.9%', label: 'Uptime' },
    { value: '24/7', label: 'Support' }
  ];

  constructor(public authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    if (this.authService.isLoggedIn) {
      this.router.navigate(['/dashboard']);
    }
  }
}
