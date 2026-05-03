import { Component, OnInit } from '@angular/core';
import { UserService } from '../../../services/user.service';
import { UserResponse } from '../../../models/auth.model';
import { ToastService } from '../../../services/toast.service';

@Component({
  selector: 'app-manage-users',
  templateUrl: './manage-users.component.html',
  styleUrls: ['./manage-users.component.css']
})
export class ManageUsersComponent implements OnInit {
  users: UserResponse[] = [];
  filteredUsers: UserResponse[] = [];
  loading = true;
  searchTerm = '';
  actionConfirmId: number | null = null;
  actionType: 'block' | 'unblock' | null = null;
  processing = false;

  constructor(
    private userService: UserService,
    private toast: ToastService
  ) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.loading = true;
    this.userService.getAllUsers().subscribe({
      next: (users) => {
        this.users = users;
        this.filteredUsers = users;
        this.loading = false;
      },
      error: () => { this.loading = false; }
    });
  }

  applyFilter(): void {
    const term = this.searchTerm.toLowerCase();
    this.filteredUsers = this.users.filter(u =>
      u.name.toLowerCase().includes(term) ||
      u.email.toLowerCase().includes(term) ||
      u.phoneNumber?.includes(term)
    );
  }

  confirmBlock(id: number): void {
    this.actionConfirmId = id;
    this.actionType = 'block';
  }

  confirmUnblock(id: number): void {
    this.actionConfirmId = id;
    this.actionType = 'unblock';
  }

  cancelAction(): void {
    this.actionConfirmId = null;
    this.actionType = null;
  }

  executeAction(id: number): void {
    if (!this.actionType) return;

    this.processing = true;
    const action$ = this.actionType === 'block'
      ? this.userService.blockUser(id)
      : this.userService.unblockUser(id);

    const isBlock = this.actionType === 'block';

    action$.subscribe({
      next: () => {
        const user = this.users.find(u => u.id === id);
        if (user) user.isActive = !isBlock;
        const filteredUser = this.filteredUsers.find(u => u.id === id);
        if (filteredUser) filteredUser.isActive = !isBlock;

        this.actionConfirmId = null;
        this.actionType = null;
        this.processing = false;
        this.toast.success(isBlock ? 'User blocked successfully.' : 'User unblocked successfully.');
      },
      error: (err) => {
        this.processing = false;
        this.toast.error(err.error?.message || `Failed to ${isBlock ? 'block' : 'unblock'} user.`);
      }
    });
  }
}
