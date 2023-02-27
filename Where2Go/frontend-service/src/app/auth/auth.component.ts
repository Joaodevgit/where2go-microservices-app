import {Component, EventEmitter, Output} from '@angular/core';
import {MatDialog} from "@angular/material/dialog";

@Component({
  selector: 'app-auth',
  templateUrl: './auth.component.html',
  styleUrls: ['./auth.component.css']
})
export class AuthComponent {

  @Output() closeDialogEvent: EventEmitter<any> = new EventEmitter<any>();
  constructor(public dialog:MatDialog) {
  }
  data: any;

  fromWhere(component:string) {
    console.log("Buton clicked from ", component)
    this.closeDialogEvent.emit();
  }
}
