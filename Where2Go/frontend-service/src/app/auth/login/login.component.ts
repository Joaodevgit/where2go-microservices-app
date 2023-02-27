import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {NotificationService} from "../../services/notification.service";
import {AuthService} from "../../services/auth.service";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  @Output() loginButtonClicked: EventEmitter<any> = new EventEmitter<any>();

  isLoading=false;

  constructor(public notification: NotificationService,
              public authService: AuthService) {
  }

  ngOnInit(): void {
    this.isLoading=false;
  }

  onLogin(loginForm: any) {
    if (loginForm.invalid){
      this.notification.showNotification("You have invalid values", "warning")
      return;
    }
    this.isLoading=true;
    console.log("Login Form", loginForm.value)
    this.authService.onLogin(loginForm.value.email, loginForm.value.password)
  }


}
