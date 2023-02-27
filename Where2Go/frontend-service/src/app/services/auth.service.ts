import {EventEmitter, Injectable, Output} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Router} from "@angular/router";
import {NotificationService} from "./notification.service";
import {UserData} from "../Models/user-data.model";
import {BehaviorSubject, Subject} from "rxjs";
import {LoginData} from "../Models/login-data.model";
import {environment} from '../../environments/environment';


@Injectable({
  providedIn: 'root'
})
export class AuthService {
  baseUrl = environment.apiUrl;
  public currentUser: any;
  private isAuthenticated = false
  private userAuthenticated: any;
  private authStatusListener = new Subject<boolean>()
  private currentUserListener = new Subject<any>();

  private tokenTimer: NodeJS.Timer;

  @Output() aClickedEvent = new EventEmitter<any>();


  constructor(private http: HttpClient, private router: Router, private notificationService: NotificationService) {
  }

  getAllUser() {
    return this.http.get(this.baseUrl + '/user-service')
  }

  getUser() {
    const currentUser = localStorage.getItem("currentUser");
    if (currentUser) {
      return JSON.parse(currentUser)
    } else {
      return null
    }
  }

  getDescrypteUser(token: string) {
    const body = {token: token}
    console.log("Getting User...")
    console.log(body)
    return this.http.post(this.baseUrl + '/user-login/decrypte', body)
  }


  onRegister(firstName: string, lastName: string, email: string, password: string) {
    var body = {firstname: firstName, lastname: lastName, email: email, password: password}

    console.log("OnRegister", body)
    return this.http.post(this.baseUrl + '/user-service', body)
  }


  // expiresin 43200
  onLogin(email: string, password: string) {
    const authData: LoginData = {email: email, password: password}


    this.http.post(this.baseUrl + '/user-login/authenticate', authData)
      .subscribe((resLogin: any) => {
        if (resLogin.token) {
          this.getDescrypteUser(resLogin.token)
            .subscribe((currentUser: any) => {
              console.log('Aqui quero testar', currentUser)
              const expiresDuration = 43200;
              this.setAuthTimer(expiresDuration)
              this.isAuthenticated = true;
              this.currentUser = currentUser;
              this.saveAuthData(currentUser, resLogin.token, new Date(new Date().getTime() + expiresDuration * 1000))
              this.authStatusListener.next(true)
              //close janela


              //this.currentUserListener.next(currentUser);
              this.AClicked()
            })
        }

      }, error => {
        this.authStatusListener.next(false)
      })
  }

  /**
   * Returns a message
   */
  onLogOut() {

    localStorage.setItem('token', 'null');
    localStorage.setItem('currentUser', 'null');
    localStorage.clear();
    this.isAuthenticated = false;
    this.router.navigate([''])
    clearTimeout(this.tokenTimer);
    this.clearAuthData();
    this.authStatusListener.next(false);


  }

  createUserKonga(username: string, custom_id: string) {
    const body = {username: username, custom_id: custom_id}
    return this.http.post(this.baseUrl + '/consumers/', body)
  }

  createTokenUser(email: string) {
    const body = {key: email, secret: "secret"}
    return this.http.post(this.baseUrl + `/consumers/${email}/jwt`, body)
  }

  private saveAuthData(user: any, token: string, expirationDate: Date) {
    localStorage.setItem('token', token)
    localStorage.setItem('expiration', expirationDate.toISOString());
    localStorage.setItem('currentUser', JSON.stringify(user))
  }

  private clearAuthData() {
    localStorage.removeItem('token');
    localStorage.removeItem('expiration');
    localStorage.removeItem('currentUser');
  }

  getIsAuth() {
    return this.isAuthenticated;
  }

  getAuthStatusListener() {
    return this.authStatusListener.asObservable();
  }

  getCurrentUserListener() {
    return this.currentUserListener.asObservable()
  }

  AClicked() {
    this.aClickedEvent.emit();
  }


  private setAuthTimer(expiresDuration: number) {
    this.tokenTimer = setTimeout(() => {
      this.onLogOut();
    }, expiresDuration * 1000);
  }

  autoAuthUser() {
    const authInformation = this.getAuthData();

    if (!authInformation) { // se nao existe info guardada
      return;
    }
    const now = new Date();
    const expiresIn = authInformation.expirationDate.getTime() - now.getTime();

    if (expiresIn > 0) {
      this.isAuthenticated = true;
      this.setAuthTimer(expiresIn / 1000);
      this.authStatusListener.next(true);
    }

  }

  private getAuthData() {
    const token = localStorage.getItem('token');
    const expirationDate = localStorage.getItem('expiration')
    const user = localStorage.getItem('currentUser')

    if (!token || !expirationDate) {
      return;
    }
    return {
      token: token,
      expirationDate: new Date(expirationDate),
      user: user
    }
  }
}
