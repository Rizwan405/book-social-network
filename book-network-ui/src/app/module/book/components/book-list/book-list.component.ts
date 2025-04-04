import { Component } from '@angular/core';
import {
  BookResponse,
  PageResponseBookResponse,
} from 'src/app/services/models';
import { OnInit } from '@angular/core';
import { BookService } from 'src/app/services/services';
import { Router } from '@angular/router';
@Component({
  selector: 'app-book-list',
  templateUrl: './book-list.component.html',
  styleUrls: ['./book-list.component.scss'],
})
export class BookListComponent implements OnInit {
  // first of all it returns the list of books
  bookResponse: PageResponseBookResponse = {};
  // component loads then call the service
  constructor(private bookService: BookService, private router: Router) {}
  page: number = 0;
  size: number = 5;
  pages: any = [];
  message = '';
  level: 'success' | 'error' = 'success';
  ngOnInit(): void {
    this.findAllbooks();
  }
  private findAllbooks() {
    this.bookService
      .findAllBooks({ page: this.page, size: this.size })
      .subscribe({
        next: (books) => {
          this.bookResponse = books;
          console.log(this.bookResponse.content);
          this.pages = Array(this.bookResponse.totalPages)
            .fill(0)
            .map((x, i) => i);
        },
        error: () => {},
      });
  }
  gotToPage(page: number) {
    this.page = page;
    this.findAllbooks();
  }

  goToFirstPage() {
    this.page = 0;
    this.findAllbooks();
  }

  goToPreviousPage() {
    this.page--;
    this.findAllbooks();
  }

  goToLastPage() {
    this.page = (this.bookResponse.totalPages as number) - 1;
    this.findAllbooks();
  }

  goToNextPage() {
    this.page++;
    this.findAllbooks();
  }

  get isLastPage() {
    return this.page === (this.bookResponse.totalPages as number) - 1;
  }
  borrowBook(book: BookResponse) {
    this.message = '';
    this.bookService.borrowBook({ 'book-id': book.id as number }).subscribe({
      next: () => {
        this.level = 'success';
        this.message = 'Book borrowed successfully';
      },
      error: (error) => {
        this.level = 'error';
        this.message = error.error.error;
      },
    });
  }
}
