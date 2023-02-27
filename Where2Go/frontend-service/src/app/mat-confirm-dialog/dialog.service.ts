import {Injectable} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {MatConfirmDialogComponent} from './mat-confirm-dialog.component';

@Injectable({
  providedIn: 'root'
})
export class DialogService {

  constructor(private dialog: MatDialog) {
  }

  openConfirmDialog(msg:any) {
    return this.dialog.open(MatConfirmDialogComponent, {
      width: '390px',
      disableClose: true,
      data: {
        message: msg
      }
    });
  }
}
