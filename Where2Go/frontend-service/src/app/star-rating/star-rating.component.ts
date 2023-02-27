import {Component, OnInit, Input} from '@angular/core';


@Component({
  selector: 'mat-star-rating',
  templateUrl: './star-rating.component.html',
  styleUrls: ['./star-rating.component.css'],
})
export class StarRatingComponent implements OnInit {

  @Input('rating') public rating: any = 0;
  fullStars = 0;
  middleStars = 0;
  emptyStars = 0;

  constructor() {
  }


  ngOnInit() {
    console.log("Rating", this.rating)
    this.starCounting(this.rating);
  }

  starCounting(rate: number) {
    var aviableStars = 3;
    this.fullStars = Math.trunc(rate)
    aviableStars = aviableStars - this.fullStars;
    var aux = rate - this.fullStars;
    if (aux % 1 != 0) {
      this.middleStars=1;
      aviableStars=aviableStars-this.middleStars
    }
    this.emptyStars=aviableStars;



  }

}
