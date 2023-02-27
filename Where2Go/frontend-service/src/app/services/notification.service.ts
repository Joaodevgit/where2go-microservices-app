import {Injectable} from '@angular/core';
import {MatSnackBar} from '@angular/material/snack-bar';
import {NotificationComponent} from "../notification/notification.component";

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  constructor(private snackBar: MatSnackBar) {
  }

  showNotification(displayMessage: string, messageType: 'error' | 'success' | 'warning') {
    this.snackBar.openFromComponent(NotificationComponent, {
      data: {
        message: displayMessage,
        type: messageType,
      },
      duration: 2000,
      horizontalPosition: 'right',
      verticalPosition: 'top',
      panelClass: [messageType]
    });
  }
}
