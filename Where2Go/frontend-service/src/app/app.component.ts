import {Component, OnInit} from '@angular/core';
import {AuthService} from "./services/auth.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit{
  title = 'FrontEnd';
  sideBarOpen = false;

  constructor(private authService: AuthService) {
  }


  sideBarToggler() {
    this.sideBarOpen= !this.sideBarOpen;
  }

  closeit() {
    this.sideBarOpen = false;
    console.log("Logging out, so sidebar is closed")
  }

  ngOnInit(): void {
    this.authService.autoAuthUser();
  }
}
