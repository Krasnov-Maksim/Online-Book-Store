package mate.academy.bookstore.service;

import static mate.academy.bookstore.config.DatabaseHelper.CATEGORY_1;
import static mate.academy.bookstore.config.DatabaseHelper.CATEGORY_1_DTO;
import static mate.academy.bookstore.config.DatabaseHelper.CATEGORY_1_DTO_WITH_ID;
import static mate.academy.bookstore.config.DatabaseHelper.CREATE_CATEGORY_1_REQUEST_DTO;
import static mate.academy.bookstore.config.DatabaseHelper.INVALID_CATEGORY_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import mate.academy.bookstore.dto.category.CategoryDto;
import mate.academy.bookstore.dto.category.CategoryDtoWithId;
import mate.academy.bookstore.dto.category.CreateCategoryRequestDto;
import mate.academy.bookstore.exception.EntityNotFoundException;
import mate.academy.bookstore.mapper.CategoryMapper;
import mate.academy.bookstore.model.Category;
import mate.academy.bookstore.repository.category.CategoryRepository;
import mate.academy.bookstore.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;
    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    @DisplayName("Verify findAll() returns all not-deleted categories in DB")
    void findAll_validPageable_ShouldReturnAllCategories() {
        //Given
        List<Category> categories = List.of(CATEGORY_1);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Category> categoryPage = new PageImpl<>(categories, pageable, categories.size());
        when(categoryRepository.findAll(any(Pageable.class)))
                .thenReturn(categoryPage);
        when(categoryMapper.toDtoWithId(any(Category.class)))
                .thenReturn(CATEGORY_1_DTO_WITH_ID);
        List<CategoryDtoWithId> expected = List.of(CATEGORY_1_DTO_WITH_ID);
        //When
        List<CategoryDtoWithId> actual = categoryService.findAll(pageable);
        //Then
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Verify getById() with valid id returns need category from DB")
    void getById_ValidId_ShouldReturnCategory() {
        CategoryDto expected = CATEGORY_1_DTO;
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(CATEGORY_1));
        when(categoryMapper.toDto(any(Category.class))).thenReturn(expected);
        CategoryDto actual = categoryService.getById(CATEGORY_1.getId());
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Verify getById() with invalid id returns exception")
    void getById_InvalidId_ShouldThrowEntityNotFoundException() {
        when(categoryRepository.findById(INVALID_CATEGORY_ID))
                .thenThrow(new EntityNotFoundException("Can't find category by id "
                        + INVALID_CATEGORY_ID));
        EntityNotFoundException entityNotFoundException = assertThrows(
                EntityNotFoundException.class, () -> categoryService.getById(INVALID_CATEGORY_ID));
        assertEquals("Can't find category by id " + INVALID_CATEGORY_ID,
                entityNotFoundException.getMessage());
        assertEquals(EntityNotFoundException.class, entityNotFoundException.getClass());
    }

    @Test
    @DisplayName("Verify save() returns correct category after saving")
    void save_ValidCreateCategoryRequestDto_ShouldSaveCategory() {
        when(categoryMapper.toModel(any(CreateCategoryRequestDto.class)))
                .thenReturn(CATEGORY_1);
        when(categoryRepository.save(any(Category.class)))
                .thenReturn(CATEGORY_1);
        CategoryDto expected = CATEGORY_1_DTO;
        when(categoryMapper.toDto(any(Category.class)))
                .thenReturn(expected);
        CategoryDto actual = categoryService.save(CREATE_CATEGORY_1_REQUEST_DTO);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Verify update() updated category with valid ID and input parameters")
    void update_ValidIdAndRequestParams_ShouldUpdateCategory() {
        //Given
        CreateCategoryRequestDto categoryDto = new CreateCategoryRequestDto("Category new name",
                "New Category Description");
        Category updatedCategory = new Category();
        updatedCategory.setId(CATEGORY_1.getId());
        updatedCategory.setName(categoryDto.name());
        updatedCategory.setDescription(categoryDto.description());
        when(categoryRepository.findById(anyLong()))
                .thenReturn(Optional.of(CATEGORY_1));
        when(categoryMapper.toModel(any(CreateCategoryRequestDto.class)))
                .thenReturn(updatedCategory);
        when(categoryRepository.save(any(Category.class)))
                .thenReturn(updatedCategory);
        CategoryDto expected = categoryMapper.toDto(updatedCategory);
        //When
        CategoryDto actual = categoryService.update(CATEGORY_1.getId(), categoryDto);
        //Then
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Verify update() throws exception for invalid ID")
    void update_InvalidId_ShouldThrowEntityNotFoundException() {
        //Given
        CreateCategoryRequestDto categoryDto = new CreateCategoryRequestDto(
                "Category with invalid id", "Category has invalid id");
        when(categoryRepository.findById(INVALID_CATEGORY_ID))
                .thenThrow(new EntityNotFoundException("Can't find category by id "
                        + INVALID_CATEGORY_ID));
        //When
        EntityNotFoundException entityNotFoundException = assertThrows(
                EntityNotFoundException.class,
                () -> categoryService.update(INVALID_CATEGORY_ID, categoryDto));
        //Then
        assertEquals("Can't find category by id " + INVALID_CATEGORY_ID,
                entityNotFoundException.getMessage());
        assertEquals(EntityNotFoundException.class, entityNotFoundException.getClass());
    }

    @Test
    @DisplayName("Verify delete() removes category by id")
    void deleteById_ValidId_ShouldDeleteCategory() {
        doNothing().when(categoryRepository).deleteById(anyLong());
        categoryService.deleteById(anyLong());
        verify(categoryRepository, times(1)).deleteById(anyLong());
    }
}
