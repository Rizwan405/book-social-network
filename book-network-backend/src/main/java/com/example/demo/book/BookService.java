package com.example.demo.book;

import com.example.demo.common.PageResponse;
import com.example.demo.exception.OperationNotPermittedException;
import com.example.demo.file.FileStorageService;
import com.example.demo.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class BookService {
    private final BookMapper mapper;
    private final BookRepository bookRepository;
    private final BookTransactionHistoryRepo transactionHistoryRepo;
    private final FileStorageService fileStorageService;
    public Integer save(BookRequest request, Authentication currentUser) {
        User user = ((User) currentUser.getPrincipal());
        Book book = mapper.toBook(request);
        book.setOwner(user);
        bookRepository.save(book);



        return  bookRepository.save(book).getId();
    }

    public BookResponse findById(Integer id) {
//        find book it should be bookEntity
//        need to convert into book response
       return bookRepository.findById(id).map(mapper::toBookResponse).orElseThrow(()-> new EntityNotFoundException("book Entity with id::" + id + "not found"));

    }

    public PageResponse<BookResponse> findAllBooks(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page,size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAllDisplayableBooks(pageable,user.getId());
        List<BookResponse> bookResponses = books.stream().map(mapper::toBookResponse).toList();
        return new PageResponse<>(bookResponses,books.getNumber(),books.getSize(),books.getTotalElements(),books.getTotalPages(),books.isFirst(),books.isLast());
    }
    public PageResponse<BookResponse> findAllBooksByOwner(int page,int size,Authentication connectedUser){
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page,size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAll(BookSpecification.withOwnerId(user.getId()),pageable);
        List<BookResponse> bookResponses = books.stream().map(mapper::toBookResponse).toList();
        return new PageResponse<>(bookResponses,books.getNumber(),books.getSize(),books.getTotalElements(),books.getTotalPages(),books.isFirst(),books.isLast());
    }

    public PageResponse<BorrowedBookResponse> findAllBorrowedBooks(int page, int size, Authentication connectedUser) {
    User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page,size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> allBorrowedBooks = transactionHistoryRepo.findAllBorrowedBooks(pageable,user.getId());
        List<BorrowedBookResponse> bookResponses = allBorrowedBooks.stream().map(mapper::toBorrowedBookResponse).toList();
        return new PageResponse<>(bookResponses,allBorrowedBooks.getNumber(),allBorrowedBooks.getSize(),allBorrowedBooks.getTotalElements(),allBorrowedBooks.getTotalPages(),allBorrowedBooks.isFirst(),allBorrowedBooks.isLast());
    }
    public PageResponse<BorrowedBookResponse> findAllReturnedBooks(int page, int size, Authentication connectedUser) {
         User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> allBorrowedBooks = transactionHistoryRepo.findAllReturnedBooks(pageable, user.getId());
        List<BorrowedBookResponse> booksResponse = allBorrowedBooks.stream()
                .map(mapper::toBorrowedBookResponse)
                .toList();
        return new PageResponse<>(
                booksResponse,
                allBorrowedBooks.getNumber(),
                allBorrowedBooks.getSize(),
                allBorrowedBooks.getTotalElements(),
                allBorrowedBooks.getTotalPages(),
                allBorrowedBooks.isFirst(),
                allBorrowedBooks.isLast()
        );
    }

    public Integer updateShareableStatus(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId).orElseThrow(()-> new EntityNotFoundException("No book with id:"+ bookId));
        User user = ((User) connectedUser.getPrincipal());
        if(!Objects.equals(book.getOwner().getId(),user.getId())){
            throw new OperationNotPermittedException("You cannot update shareable status");

        }
        book.setShareable(!book.isShareable());
        bookRepository.save(book);
        return bookId;
    }

    public Integer updateArchivedStatus(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId).orElseThrow(()-> new EntityNotFoundException("No book with id:"+ bookId));
        User user = ((User) connectedUser.getPrincipal());
        if(!Objects.equals(book.getOwner().getId(),user.getId())){
            throw new OperationNotPermittedException("You cannot update archived status");

        }
        book.setArchived(!book.isArchived());
        bookRepository.save(book);
        return bookId;
    }

    public Integer borrowBook(Integer bookId, Authentication connectedUser) {
//        fetch the book
//        check if it is not archived, borrowed, and the user is owner
        Book book = bookRepository.findById(bookId).orElseThrow(()-> new EntityNotFoundException("No book with id:"+ bookId));
        User user = ((User) connectedUser.getPrincipal());
        if(book.isArchived() || book.isShareable()){
            throw new OperationNotPermittedException("The requested book cannot be borrowed since it is archived or not shareable");
        }
        if(Objects.equals(book.getOwner().getId(),user.getId())){
            throw new OperationNotPermittedException("Cannot borrow your own book");
        }
        final boolean alreadyBorrowed = transactionHistoryRepo.isAlreadyBorrowedByUser(bookId,user.getId());
        if(alreadyBorrowed){
           throw new OperationNotPermittedException("Book is already borrowed by someone");
        }
        BookTransactionHistory transactionHistory = BookTransactionHistory.builder().user(user).book(book).returnApproved(false).returned(false).build();
        return transactionHistoryRepo.save(transactionHistory).getId();
    }

    public Integer returnBorrowedBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId).orElseThrow(()-> new EntityNotFoundException("No book with id:"+ bookId));
        User user = ((User) connectedUser.getPrincipal());
        if(book.isArchived() || book.isShareable()){
            throw new OperationNotPermittedException("Cannot Complete the operation since book is already borrowed");
        }
        if(Objects.equals(book.getOwner().getId(),user.getId())){
            throw new OperationNotPermittedException("Cannot borrow or return  your own book");
        }
        BookTransactionHistory transactionHistory = transactionHistoryRepo.findByBookIdAndUserId(bookId,user.getId()).orElseThrow(()->new OperationNotPermittedException("You cannot borrow/return this book this book"));
        transactionHistory.setReturned(true);
         return transactionHistoryRepo.save(transactionHistory).getId();

    }

    public Integer approveReturnBorrowedBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId).orElseThrow(()-> new EntityNotFoundException("No book with id:"+ bookId));
        User user = ((User) connectedUser.getPrincipal());
        if(book.isArchived() || book.isShareable()){
            throw new OperationNotPermittedException("Cannot Complete the operation since book is already borrowed");
        }
        if(!Objects.equals(book.getOwner().getId(),user.getId())){
            throw new OperationNotPermittedException("Cannot borrow or return  your own book");
        }
        BookTransactionHistory transactionHistory = transactionHistoryRepo.findByBookIdAndOwnerId(bookId,user.getId()).orElseThrow(()->new OperationNotPermittedException("This book is not returned yet and cannot change return "));
        transactionHistory.setReturned(true);
        return transactionHistoryRepo.save(transactionHistory).getId();
    }

    public void uploadCoverPicture(Integer bookId, MultipartFile file, Authentication currentUser) {
        Book book = bookRepository.findById(bookId).orElseThrow(()-> new EntityNotFoundException("No book with id:"+ bookId));
        User user = ((User) currentUser.getPrincipal());
        var bookCover = fileStorageService.saveFile(file,book,user.getId());
        book.setBookCover(bookCover);
        bookRepository.save(book);
    }
    public List<BookResponse> getAllBooks(){
//        convert into book response

       return  bookRepository.findAll().stream().map(mapper::toBookResponse).toList();
    }
}
