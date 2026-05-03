import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UserService } from '../../services/user.service';
import { AuthService } from '../../services/auth.service';
import { UserResponse } from '../../models/auth.model';
import { ToastService } from '../../services/toast.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {
  user: UserResponse | null = null;
  form: FormGroup;
  loading = true;
  saving = false;
  editing = false;

  constructor(
    private userService: UserService,
    public authService: AuthService,
    private fb: FormBuilder,
    private toast: ToastService
  ) {
    this.form = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2)]],
      phoneNumber: ['', [Validators.required, Validators.pattern(/^[6-9]\d{9}$/)]]
    });
  }

  ngOnInit(): void {
    this.userService.getProfile().subscribe({
      next: (user) => {
        this.user = user;
        this.form.patchValue({ name: user.name, phoneNumber: user.phoneNumber });
        this.loading = false;
      },
      error: () => { this.loading = false; }
    });
  }

  startEdit(): void {
    this.editing = true;
  }

  cancelEdit(): void {
    this.editing = false;
    if (this.user) {
      this.form.patchValue({ name: this.user.name, phoneNumber: this.user.phoneNumber });
    }
  }

  saveProfile(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.saving = true;

    this.userService.updateProfile(this.form.value).subscribe({
      next: (user) => {
        this.user = user;
        this.saving = false;
        this.editing = false;
        this.toast.success('Profile updated successfully!');
      },
      error: (err) => {
        this.saving = false;
        this.toast.error(err.error?.message || 'Failed to update profile');
      }
    });
  }

  get name() { return this.form.get('name'); }
  get phoneNumber() { return this.form.get('phoneNumber'); }
}
