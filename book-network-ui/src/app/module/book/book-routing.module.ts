import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MainComponent } from './pages/main/main.component';
import { BookListComponent } from './components/book-list/book-list.component';
import { MyBooksComponent } from './components/my-books/my-books.component';
import { ManageBookComponent } from './pages/manage-book/manage-book.component';
import { BorroweBookComponent } from './pages/borrowe-book/borrowe-book.component';
import { ReturnedBookComponent } from './components/returned-book/returned-book.component';
import { authGuard } from 'src/app/services/guard /auth.guard';
const routes: Routes = [
  {
    path: '',
    component: MainComponent,
    canActivate: [authGuard],
    children: [
      {
        path: '',
        component: BookListComponent,
        canActivate: [authGuard],
      },
      {
        path: 'my-books',
        component: MyBooksComponent,
        canActivate: [authGuard],
      },
      {
        path: 'manage',
        component: ManageBookComponent,
        canActivate: [authGuard],
      },
      {
        path: 'manage/:id',
        component: ManageBookComponent,
        canActivate: [authGuard],
      },
      {
        path: 'my-borrowed-books',
        component: BorroweBookComponent,
        canActivate: [authGuard],
      },
      {
        path: 'my-returned-books',
        component: ReturnedBookComponent,
        canActivate: [authGuard],
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class BookRoutingModule {}
