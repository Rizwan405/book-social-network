import { Component } from '@angular/core';
import { Input } from '@angular/core';
@Component({
  selector: 'app-rating',
  templateUrl: './rating.component.html',
  styleUrls: ['./rating.component.scss'],
})
export class RatingComponent {
  @Input() rating: number = 0;
  maxRating: number = 5;
  // full star
  // half star
  // empty star
  get fullStars(): number {
    return Math.floor(this.rating);
  }
  get emptyStars(): number {
    return this.maxRating - Math.ceil(this.rating);
  }

  get hasHalfStar(): boolean {
    return this.rating % 1 !== 0;
  }
}
