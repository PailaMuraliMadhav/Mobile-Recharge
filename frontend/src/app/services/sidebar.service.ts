import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class SidebarService {
  private _open = new BehaviorSubject<boolean>(false);
  open$ = this._open.asObservable();

  toggle(): void { this._open.next(!this._open.value); }
  close(): void  { this._open.next(false); }
  get isOpen(): boolean { return this._open.value; }
}
