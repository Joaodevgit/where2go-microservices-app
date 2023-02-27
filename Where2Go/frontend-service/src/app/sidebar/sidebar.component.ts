import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {AuthService} from "../services/auth.service";
import {Subscription} from "rxjs";

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent implements OnInit, OnDestroy{

  @Output() close: EventEmitter<any> = new EventEmitter();

  currentUserSub:Subscription;
  currentUser:any | null;
  nameUser: string;

  constructor(private authService: AuthService) {
  }

  @Input() position = 'end'


  onLogOut() {
    this.authService.onLogOut();
  }

  ngOnInit(): void {
    this.checkIfUserisAlreadyLogged()
    this.currentUserSub = this.authService.getCurrentUserListener()
      .subscribe((user:any)=>{
        //console.log("From sideBar", user)
        this.currentUser=user;
        this.nameUser = user.firstname +' '+user.lastname;
      })

  }
  ngOnDestroy() {
    this.currentUserSub.unsubscribe();
  }

  closeSideBar() {
    //console.log("FROM SIDEBAR FECHAR TUDO")
    this.close.emit(false);
  }

  private checkIfUserisAlreadyLogged() {
    let userIsAuthenticated = this.authService.getIsAuth();
    if (userIsAuthenticated === true){
      this.currentUser = this.authService.getUser();
      this.nameUser = this.currentUser.firstname +' '+this.currentUser.lastname
    }

  }
}
