import {Injectable} from '@angular/core';
import {NotificationService} from "./notification.service";
import {Subject, Subscription} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class ItineraryBucketService {

  private lengthListener = new Subject<boolean>()
  private pointListener = new Subject<any>();

  private items: any = [];

  constructor(private notification: NotificationService) {
  }

  addPoint(item: any) {
    let existingItem = this.items.find((i: any) => i.xid === item.xid)
    if (existingItem === undefined) {
      this.items.push(item)
      this.lengthListener.next(this.items.length)
      this.notification.showNotification("Point added", "success")
    } else {
      this.notification.showNotification('Already there', 'success');
    }
  }

  removeFromHere(item: any) {
    this.items = this.items.filter((i: any) => i.xid !== item.xid);
    this.pointListener.next(this.items)
    this.lengthListener.next(this.items.length)
  }

  getAllPointsUpdated(){
    return this.pointListener.asObservable();
  }

  getItems() {
    return this.items;
  }

  clear() {
    this.items = [];
    this.lengthListener.next(this.items.length)
    return this.items;
  }

  returnLengthListesnerUpdated() {
    return this.lengthListener.asObservable();
  }
}
