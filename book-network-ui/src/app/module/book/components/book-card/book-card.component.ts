import { Component, Input } from '@angular/core';
import { BookResponse } from 'src/app/services/models';
import { Output } from '@angular/core';
import { EventEmitter } from '@angular/core';
@Component({
  selector: 'app-book-card',
  templateUrl: './book-card.component.html',
  styleUrls: ['./book-card.component.scss'],
})
export class BookCardComponent {
  private _book: BookResponse = {};
  private _bookCover: string | undefined;
  private _manage: boolean = false;
  @Input()
  set book(book: BookResponse) {
    this._book = book;
  }
  get book(): BookResponse {
    return this._book;
  }

  get bookCover(): string | undefined {
    if (this._book.cover) {
      return 'data:image/jpg;base64,' + this._book.cover;
    }

    return `https://picsum.photos/id/559/1900/800`;
  }
  get manage(): boolean {
    return this._manage;
  }
  @Input()
  set manage(value: boolean) {
    this._manage = value;
  }
  // deifining a event emiters
  @Output()
  private share: EventEmitter<BookResponse> = new EventEmitter<BookResponse>();
  @Output()
  private addToWaiting: EventEmitter<BookResponse> =
    new EventEmitter<BookResponse>();
  @Output()
  private borrow: EventEmitter<BookResponse> = new EventEmitter<BookResponse>();
  @Output()
  private edit: EventEmitter<BookResponse> = new EventEmitter<BookResponse>();
  @Output()
  private archive: EventEmitter<BookResponse> =
    new EventEmitter<BookResponse>();

  // methods to implement
  onShowDetails() {
    this.share.emit(this._book);
  }
  onBorrow() {
    this.borrow.emit(this._book);
  }
  onAddToWaitingList() {
    this.addToWaiting.emit(this._book);
  }
  onEdit() {
    this.edit.emit(this._book);
  }
  onShare() {
    this.share.emit(this._book);
  }
  onArchive() {
    this.archive.emit(this._book);
  }
}
