#include "gtest/gtest.h"

import Containers;

#include <string>
#include <strstream>

namespace Containers::Tests
{
	template<typename T>
	void ExpectEqualsButNotSame(const DynamicArray<T>& arr1, const DynamicArray<T>& arr2)
	{
		ASSERT_EQ(arr1.GetSize(), arr2.GetSize()) << "Arrays have different sizes";
		for (int i = 0; i < arr1.GetSize(); ++i)
		{
			const T& e1 = arr1[i];
			const T& e2 = arr2[i];

			EXPECT_EQ(e1, e2) << 
				(std::stringstream() << "The values at position " << i << " are different (" << e1 << "and "<< e2 <<")").str().c_str();

			EXPECT_NE(&e1, &e2) <<
				(std::stringstream() <<
					"The values at position " << i << 
				    " have the same addresses (" << reinterpret_cast<const void*>(&e1) << " and " << reinterpret_cast<const void*>(&e2) << ")"
				).str().c_str();
		}
	}

	template<typename T>
	void TestDefaultConstructor()
	{
		const size_t size = 0;
		DynamicArray<T> arr;
		EXPECT_EQ(size, arr.GetSize());
	}

	template<typename T, size_t N>
	void TestConversionConstructor()
	{
		DynamicArray<T> arr(N);
		ASSERT_EQ(N, arr.GetSize());
		for (int i = 0; i < N; ++i)
			EXPECT_EQ(T{}, arr[i]);
	}

	template<typename T, size_t N>
	void TestCreateConstObjectWithConversionConstructor()
	{
		const DynamicArray<T> arr(N);
		ASSERT_EQ(N, arr.GetSize());
		for (int i = 0; i < N; ++i)
			EXPECT_EQ(T{}, arr[i]);
	}

	template<typename T, size_t N>
	void TestCreateObjectWithInitializerList(T (&init)[N])
	{
		const DynamicArray<T> arr = std::initializer_list<T>(std::begin(init), std::end(init));
		ASSERT_EQ(N, arr.GetSize());
		for (int i = 0; i < N; ++i)
			EXPECT_EQ(init[i], arr[i]);
	}

	template<typename T, size_t N>
	void TestCopyConstructor(T(&init)[N])
	{
		DynamicArray<T> arr1 = std::initializer_list<T>(std::begin(init), std::end(init));
		DynamicArray<T> arr2 = arr1;

		ExpectEqualsButNotSame(arr1, arr2);
	}

	template<typename T, size_t N>
	void TestMoveConstructor(T(&init)[N])
	{
		T* expectedAddresses[N] = {};

		DynamicArray<T> arr1 = std::initializer_list<T>(std::begin(init), std::end(init));
		for (int i = 0; i < arr1.GetSize(); ++i)
			expectedAddresses[i] = std::addressof(arr1[i]);

		DynamicArray<T> arr2 = std::move(arr1);
		ASSERT_EQ(N, arr2.GetSize());
		for (int i = 0; i < arr2.GetSize(); ++i)
		{
			EXPECT_EQ(expectedAddresses[i], std::addressof(arr2[i]));
			EXPECT_EQ(init[i], arr2[i]);
		}
		
		for (int i = 0; i < arr1.GetSize(); ++i)
			EXPECT_NE(expectedAddresses[i], std::addressof(arr1[i]));
	}

	template<typename T, size_t N, size_t M>
	void TestCopyAssignment(T(&init1)[N], T(&init2)[M])
	{
		DynamicArray<T> arr1 = std::initializer_list<T>(std::begin(init1), std::end(init1));
		DynamicArray<T> arr2 = std::initializer_list<T>(std::begin(init2), std::end(init2));
		arr2 = arr1;
		ExpectEqualsButNotSame(arr1, arr2);
	}

	template<typename T, size_t N>
	void TestSelfCopyAssignment(T(&init)[N])
	{
		T* expectedAddresses[N] = {};

		DynamicArray<T> arr1 = std::initializer_list<T>(std::begin(init), std::end(init));
		for (int i = 0; i < arr1.GetSize(); ++i)
			expectedAddresses[i] = std::addressof(arr1[i]);

		arr1 = arr1;
		ASSERT_EQ(N, arr1.GetSize());
		for (int i = 0; i < arr1.GetSize(); ++i)
		{
			EXPECT_EQ(expectedAddresses[i], std::addressof(arr1[i]));
			EXPECT_EQ(init[i], arr1[i]);
		}
	}

	template<typename T, size_t N, size_t M>
	void TestMoveAssignment(T(&init1)[N], T(&init2)[M])
	{
		T* expectedAddresses[N] = {};

		DynamicArray<T> arr1 = std::initializer_list<T>(std::begin(init1), std::end(init1));
		for (int i = 0; i < arr1.GetSize(); ++i)
			expectedAddresses[i] = std::addressof(arr1[i]);

		DynamicArray<T> arr2 = std::initializer_list<T>(std::begin(init2), std::end(init2));
		arr2 = std::move(arr1);

		ASSERT_EQ(N, arr2.GetSize());
		for (int i = 0; i < arr2.GetSize(); ++i)
		{
			EXPECT_EQ(expectedAddresses[i], std::addressof(arr2[i]));
			EXPECT_EQ(init1[i], arr2[i]);
		}

		for (int i = 0; i < arr1.GetSize(); ++i)
			EXPECT_NE(expectedAddresses[i], std::addressof(arr1[i]));
	}

	template<typename T, size_t N>
	void TestSelfMoveAssignment(T(&init)[N])
	{
		T* expectedAddresses[N] = {};

		DynamicArray<T> arr1 = std::initializer_list<T>(std::begin(init), std::end(init));
		for (int i = 0; i < arr1.GetSize(); ++i)
			expectedAddresses[i] = std::addressof(arr1[i]);

		arr1 = std::move(arr1);
		ASSERT_EQ(N, arr1.GetSize());
		for (int i = 0; i < arr1.GetSize(); ++i)
		{
			EXPECT_EQ(expectedAddresses[i], std::addressof(arr1[i]));
			EXPECT_EQ(init[i], arr1[i]);
		}
	}

	template<typename T, size_t N, size_t M>
	void TestCopyAssignmentFromInintializationList(T(&init)[N], T(&assignment)[M])
	{
		DynamicArray<T> arr1 = std::initializer_list<T>(std::begin(init), std::end(init));
		arr1 = std::initializer_list<T>(std::begin(assignment), std::end(assignment));
		for (int i = 0; i < arr1.GetSize(); ++i)
		{
			EXPECT_EQ(assignment[i], arr1[i]);
		}
	}

	template<typename T, size_t N, size_t INDEX>
	void TestModifyElement(T(&init)[N], const T& element)
	{
		static_assert(INDEX < N);
		DynamicArray<T> arr1 = std::initializer_list<T>(std::begin(init), std::end(init));
		arr1[INDEX] = element;
		EXPECT_EQ(element, arr1[INDEX]);
	}

	template<typename T, size_t N>
	void TestForRangeLoopByValue(T(&init)[N])
	{
		DynamicArray<T> arr1 = std::initializer_list<T>(std::begin(init), std::end(init));
		int i = 0;
		for (auto element : arr1)
		{
			EXPECT_EQ(arr1[i], element);
			EXPECT_NE(std::addressof(arr1[i]), std::addressof(element));
			++i;
		}
	}

	template<typename T, size_t N>
	void TestForRangeLoopByReference(T(&init)[N])
	{
		DynamicArray<T> arr1 = std::initializer_list<T>(std::begin(init), std::end(init));
		int i = 0;
		for (auto& element : arr1)
		{
			EXPECT_EQ(arr1[i], element);
			EXPECT_EQ(std::addressof(arr1[i]), std::addressof(element));
			++i;
		}
	}

	template<typename T, size_t N>
	void TestModifyElementInForRangeLoop(T(&init)[N])
	{
		DynamicArray<T> arr1 = std::initializer_list<T>(std::begin(init), std::end(init));
		int i = 0;
		for (auto& element : arr1)
		{
			element *= 2;
			init[i] *= 2;
			EXPECT_EQ(std::addressof(arr1[i]), std::addressof(element));
			++i;
		}

		for (int i = 0; i < arr1.GetSize(); ++i)
		{
			EXPECT_EQ(init[i], arr1[i]);
		}
	}

	template<typename T, size_t N>
	void TestForRangeLoopForConstObjectByReference(T(&init)[N])
	{
		const DynamicArray<T> arr1 = std::initializer_list<T>(std::begin(init), std::end(init));
		int i = 0;
		for (auto& element : arr1)
		{
			EXPECT_EQ(std::addressof(arr1[i]), std::addressof(element));
			++i;
		}
	}

	TEST(DynamicArrayTest, TestDefaultConstructor)
	{
		::Containers::Tests::TestDefaultConstructor<int>();
		::Containers::Tests::TestDefaultConstructor<long long>();
	}

	TEST(DynamicArrayTest, TestConversionConstructor)
	{
		::Containers::Tests::TestConversionConstructor<int, 5>();
		::Containers::Tests::TestConversionConstructor<long long, 5>();
	}

	TEST(DynamicArrayTest, TestCreateConstObjectWithConversionConstructor)
	{
		::Containers::Tests::TestCreateConstObjectWithConversionConstructor<int, 5>();
		::Containers::Tests::TestCreateConstObjectWithConversionConstructor<long long, 5>();
	}

	TEST(DynamicArrayTest, TestCreateObjectWithInitializerList)
	{
		int init1[] = { 1,2,3,4,5,6,7,8,9 };
		::Containers::Tests::TestCreateObjectWithInitializerList(init1);

		long long init2[] = { 1,2,3,4,5,6,7,8,9 };
		::Containers::Tests::TestCreateObjectWithInitializerList(init2);
	}

	TEST(DynamicArrayTest, TestCopyConstructor)
	{
		int init1[] = { 1,2,3,4,5,6,7,8,9 };
		::Containers::Tests::TestCopyConstructor(init1);

		long long init2[] = { 1,2,3,4,5,6,7,8,9 };
		::Containers::Tests::TestCopyConstructor(init2);
	}

	TEST(DynamicArrayTest, TestMoveConstructor)
	{
		int init1[] = { 1,2,3,4,5,6,7,8,9 };
		::Containers::Tests::TestMoveConstructor(init1);

		long long init2[] = { 1,2,3,4,5,6,7,8,9 };
		::Containers::Tests::TestMoveConstructor(init2);
	}

	TEST(DynamicArrayTest, TestCopyAssignment)
	{
		int init1[] = {1,2,3,4,5,6,7,8,9};
		int init2[] = {10,20,30,40,50};
		::Containers::Tests::TestCopyAssignment(init1, init2);

		long long init3[] = {1,2,3,4,5,6,7,8,9};
		long long init4[] = { 10,20,30,40,50 };
		::Containers::Tests::TestCopyAssignment(init3, init4);
	}
	
	TEST(DynamicArrayTest, TestSelfCopyAssignment)
	{
		int init1[] = { 1,2,3,4,5,6,7,8,9 };
		::Containers::Tests::TestSelfCopyAssignment(init1);

		long long init2[] = {1,2,3,4,5,6,7,8,9};
		::Containers::Tests::TestSelfCopyAssignment(init2);
	}
	TEST(DynamicArrayTest, TestMoveAssignment)
	{
		int init1[] = { 1,2,3,4,5,6,7,8,9 };
		int init2[] = { 10,20,30,40,50 };
		::Containers::Tests::TestMoveAssignment(init1, init2);

		long long init3[] = { 1,2,3,4,5,6,7,8,9 };
		long long init4[] = { 10,20,30,40,50 };
		::Containers::Tests::TestMoveAssignment(init3, init4);
	}

	TEST(DynamicArrayTest, TestSelfMoveAssignment)
	{
		int init1[] = { 1,2,3,4,5,6,7,8,9 };
		::Containers::Tests::TestSelfMoveAssignment(init1);

		long long init2[] = { 1,2,3,4,5,6,7,8,9 };
		::Containers::Tests::TestSelfMoveAssignment(init2);
	}

	TEST(DynamicArrayTest, TestCopyAssignmentFromInintializationList)
	{
		int init1[] = { 10,20,30,40,50 };
		int assignment1[] = { 1,2,3,4,5,6,7,8,9 };
		::Containers::Tests::TestCopyAssignmentFromInintializationList(init1, assignment1);

		long long init2[] = {10,20,30,40,50};
		long long assignment2[] = { 1,2,3,4,5,6,7,8,9 };
		::Containers::Tests::TestCopyAssignmentFromInintializationList(init2, assignment2);
	}

	TEST(DynamicArrayTest, TestModifyElement)
	{
		int init1[] = { 1,2,3,4,5,6,7,8,9 };
		::Containers::Tests::TestModifyElement<int, 9, 2>(init1, 45);

		long long init2[] = {1,2,3,4,5,6,7,8,9};
		::Containers::Tests::TestModifyElement<long long, 9, 2>(init2, 45LL);
	}

	TEST(DynamicArrayTest, TestForRangeLoopByValue)
	{
		int init1[] = { 1,2,3,4,5,6,7,8,9 };
		::Containers::Tests::TestForRangeLoopByValue(init1);

		long long init2[] = { 1,2,3,4,5,6,7,8,9 };
		::Containers::Tests::TestForRangeLoopByValue(init2);
	}

	TEST(DynamicArrayTest, TestForRangeLoopByReference)
	{
		int init1[] = { 1,2,3,4,5,6,7,8,9 };
		::Containers::Tests::TestForRangeLoopByReference(init1);

		long long init2[] = { 1,2,3,4,5,6,7,8,9 };
		::Containers::Tests::TestForRangeLoopByReference(init2);
	}

	TEST(DynamicArrayTest, TestModifyElementInForRangeLoop)
	{
		int init1[] = { 1,2,3,4,5,6,7,8,9 };
		::Containers::Tests::TestModifyElementInForRangeLoop(init1);

		long long init2[] = { 1,2,3,4,5,6,7,8,9 };
		::Containers::Tests::TestModifyElementInForRangeLoop(init2);
	}
	TEST(DynamicArrayTest, TestForRangeLoopForConstObjectByReference)
	{
		int init1[] = { 1,2,3,4,5,6,7,8,9 };
		::Containers::Tests::TestForRangeLoopForConstObjectByReference(init1);

		long long init2[] = { 1,2,3,4,5,6,7,8,9 };
		::Containers::Tests::TestForRangeLoopForConstObjectByReference(init2);
	}
}
