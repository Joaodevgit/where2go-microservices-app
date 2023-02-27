import {Component, EventEmitter, Output} from '@angular/core';
import {AuthService} from "../../services/auth.service";
import {NotificationService} from "../../services/notification.service";

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {
  isLoading=false;

  constructor(public authService: AuthService, public notification: NotificationService) {

  }

  onRegister(registerForm: any) {
    console.log("Register Form", registerForm.value)
    this.isLoading=true;
    if (registerForm.invalid) {
      this.notification.showNotification("You have invalid values", "warning")
      this.isLoading=false;
      return;
    }

    if (registerForm.value.password !== registerForm.value.confirmPassword) {
      this.notification.showNotification("The passwords don't combine", 'warning');
      this.isLoading=false;
      return;
    }

    this.authService.onRegister(registerForm.value.firstName, registerForm.value.lastName, registerForm.value.email, registerForm.value.password)
      .subscribe((resRegister: any) => {
        this.authService.createUserKonga(resRegister.email, resRegister.id.toString())
          .subscribe((resKongaCreate: any) => {
            this.authService.createTokenUser(resKongaCreate.username)
              .subscribe((resKongaToken) => {
                this.notification.showNotification("Registation was success", "success")
                this.authService.AClicked();
                this.isLoading=false;
              })

          }, error => {
            console.log(error.error.message)
            this.notification.showNotification(error.error.message, "error")
          })
      }, error => {
        console.log(error.error.message)
        this.notification.showNotification(error.error.message, "error")
      })

  }

}
