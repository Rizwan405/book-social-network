import { Component } from '@angular/core';
import { BookService } from 'src/app/services/services';
import { OnInit } from '@angular/core';
import {
  BookResponse,
  FeedBackRequest,
  PageResponseBorrowedBookResponse,
} from 'src/app/services/models';
import { FeedbackService } from 'src/app/services/services';
import { returnBorrowBook } from 'src/app/services/fn/book/return-borrow-book';
@Component({
  selector: 'app-borrowe-book',
  templateUrl: './borrowe-book.component.html',
  styleUrls: ['./borrowe-book.component.scss'],
})
export class BorroweBookComponent {
  constructor(
    private bookService: BookService,
    private feedbackService: FeedbackService
  ) {}
  borrowedBooks: PageResponseBorrowedBookResponse = {};
  page: number = 0;
  size: number = 5;
  pages: number[] = [];
  selectedBook: BookResponse | undefined = undefined;
  feedbackRequest: FeedBackRequest = { bookId: 0, note: 0, comment: '' };
  ngOnInit(): void {
    this.findAllBorrowedBooks();
  }
  private findAllBorrowedBooks() {
    this.bookService
      .findAllBorrowedBooks({ page: this.page, size: this.size })
      .subscribe({
        next: (resp) => {
          this.borrowedBooks = resp;
          this.pages = Array(this.borrowedBooks.totalPages)
            .fill(0)
            .map((x, i) => i);
        },
        error: (err) => {
          console.log(err);
        },
      });
  }

  gotToPage(page: number) {
    this.page = page;
    this.findAllBorrowedBooks();
  }

  goToFirstPage() {
    this.page = 0;
    this.findAllBorrowedBooks();
  }

  goToPreviousPage() {
    this.page--;
    this.findAllBorrowedBooks();
  }

  goToLastPage() {
    this.page = (this.borrowedBooks.totalPages as number) - 1;
    this.findAllBorrowedBooks();
  }

  goToNextPage() {
    this.page++;
    this.findAllBorrowedBooks();
  }

  get isLastPage() {
    return this.page === (this.borrowedBooks.totalPages as number) - 1;
  }

  returnBorrowedBook(book: any) {
    this.selectedBook = book;
    this.feedbackRequest.bookId = book.id as number;
  }

  returnBook(withFeedback: boolean) {
    this.bookService
      .returnBorrowBook({
        'book-id': this.selectedBook?.id as number,
      })
      .subscribe({
        next: () => {
          if (withFeedback) {
            this.giveFeedback();
          }
          this.selectedBook = undefined;
          this.findAllBorrowedBooks();
        },
      });
  }

  private giveFeedback() {
    this.feedbackService
      .saveFeedBack({
        body: this.feedbackRequest,
      })
      .subscribe({
        next: () => {},
      });
  }
}
