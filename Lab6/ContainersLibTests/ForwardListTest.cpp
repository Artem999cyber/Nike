#if defined(FORCE_RUN_ALL_TESTS) || RUN_FORWARD_LIST_TESTS == 1
#include "gtest/gtest.h"
#include <memory>

import Containers;

std::atomic<unsigned long long> heapBlocksCounter = 0;

void* operator new(std::size_t size) noexcept(false)
{
	if (size == 0)
		++size; // avoid std::malloc(0) which may return nullptr on success

	if (void* ptr = std::malloc(size))
	{
		heapBlocksCounter.fetch_add(1);
		return ptr;
	}

	throw std::bad_alloc{};
}

void operator delete(void* ptr) noexcept
{
	free(ptr);
	heapBlocksCounter.fetch_sub(1);
}

namespace Containers::Tests
{
	class Countable
	{
	public:
		Countable(int x, bool throwOnCopy = false, unsigned copyCount = 0) noexcept
			: x{x}
			, throwOnCopy{throwOnCopy}
			, copiesLeft(copyCount)
		{
			m_counter.fetch_add(1);
		}

		Countable(const Countable& other) 
			: x{ other.x }
			, throwOnCopy{other.throwOnCopy}
			, copiesLeft{ other.throwOnCopy ? other.copiesLeft - 1 : other.copiesLeft }

		{
			if (other.throwOnCopy && other.copiesLeft == 0)
				throw std::exception{ "There are no copies available" };
			m_counter.fetch_add(1);
		}

		Countable(Countable&& other) noexcept : x{ other.x }
		{
			m_counter.fetch_add(1);
		}

		~Countable()
		{
			m_counter.fetch_sub(1);
		}

		bool operator ==(const Countable& other) const noexcept
		{
			return x == other.x;
		}

		static unsigned long long GetCounter() { return m_counter.load(); }

	private:
		static std::atomic<unsigned long long> m_counter;
		int x;
		bool throwOnCopy;
		unsigned copiesLeft;
	};

	std::atomic<unsigned long long> Countable::m_counter{ 0 };


	template<typename ExpectedForwardIt, typename ActualForwardIt>
	void ExpectEquals(ExpectedForwardIt expectedBegin, ExpectedForwardIt expectedEnd, ActualForwardIt actualBegin, ActualForwardIt actualEnd, bool expectSameAddresses)
	{
		auto expectedIt = expectedBegin;
		auto actualIt = actualBegin;

		while ((expectedIt != expectedEnd || actualIt != actualEnd))
		{
			ASSERT_EQ(*expectedIt, *actualIt);
			if (expectSameAddresses)
				ASSERT_EQ(std::addressof(*expectedIt), std::addressof(*actualIt));
			else
				ASSERT_NE(std::addressof(*expectedIt), std::addressof(*actualIt));
			++expectedIt;
			++actualIt;
		}

		EXPECT_EQ(expectedEnd, expectedIt);
		EXPECT_EQ(actualEnd, actualIt);
	}

	template<typename ExpectedForwardIt, typename ExpectedAddressesForwardIt, typename ActualForwardIt>
	void ExpectEquals(ExpectedForwardIt expectedBegin, 
					  ExpectedForwardIt expectedEnd, 
					  ExpectedAddressesForwardIt expectedAddressesBegin,
		              ExpectedAddressesForwardIt expectedAddressesEnd,
					  ActualForwardIt actualBegin, 
					  ActualForwardIt actualEnd, 
					  bool expectAddressesAreEquals)
	{
		auto expectedIt = expectedBegin;
		auto expectedAddressIt = expectedAddressesBegin;
		auto actualIt = actualBegin;

		while ((expectedIt != expectedEnd || actualIt != actualEnd))
		{
			ASSERT_EQ(*expectedIt, *actualIt);
			if (expectAddressesAreEquals)
				ASSERT_EQ(*expectedAddressIt, std::addressof(*actualIt));
			else
				ASSERT_NE(*expectedAddressIt, std::addressof(*actualIt));
			++expectedIt;
			++expectedAddressIt;
			++actualIt;
		}

		EXPECT_EQ(expectedEnd, expectedIt);
		EXPECT_EQ(actualEnd, actualIt);
	}

	template<typename T>
	void ExpectEquals(const std::initializer_list<T>& expected, const ForwardList<T>& actual)
	{
		EXPECT_EQ(expected.size(), actual.GetSize());
		return ExpectEquals(expected.begin(), expected.end(), actual.begin(), actual.end(), false);
	}

	template<typename T>
	void ExpectEquals(
		const std::initializer_list<T>& expected, 
		const std::vector<T*>& expectedAddresses, 
		const Containers::ForwardList<T>& actual,
		bool expectAddressesAreEquals)
	{
		EXPECT_EQ(expected.size(), actual.GetSize());
		return ExpectEquals(
			expected.begin(), 
			expected.end(),
			expectedAddresses.begin(),
			expectedAddresses.end(),
			actual.begin(), 
			actual.end(), 
			expectAddressesAreEquals
		);
	}

	template<typename T>
	void ExpectEquals(const ForwardList<T>& expected, const ForwardList<T>& actual, bool expectSameAddresses)
	{
		EXPECT_EQ(expected.GetSize(), actual.GetSize());
		return ExpectEquals(expected.begin(), expected.end(), actual.begin(), actual.end(), expectSameAddresses);
	}

	template <typename T>
	void TestDefaultConstructor()
	{
		ForwardList<T> list;
		ExpectEquals(std::initializer_list<T>{}, list);
	}

	template <typename T>
	void TestConstructorWithInitializerList(const std::initializer_list<T>& initList)
	{
		ForwardList<T> list = initList;
		ExpectEquals(initList, list);
	}

	template <typename T>
	void TestCopyConstructor(const std::initializer_list<T>& initList)
	{
		ForwardList<T> list1 = initList;
		ForwardList<T> list2 = list1;
		ExpectEquals(list1, list2, false);
	}

	template <typename T>
	void TestCopyConstructorOnHeap(const std::initializer_list<T>& initList)
	{
		ForwardList<T> list1 = initList;
		auto list2 = std::make_unique<ForwardList<T>>(list1);
		ExpectEquals(list1, *list2, false);
	}

	template <typename T>
	void TestMoveConstructor(const std::initializer_list<T>& initList)
	{
		ForwardList<T> list1 = initList;

		std::vector<T*> expectedAddresses;
		expectedAddresses.reserve(initList.size());

		for (auto& element : list1)
			expectedAddresses.push_back(std::addressof(element));

		ForwardList<T> list2 = std::move(list1);
		
		ExpectEquals(initList, expectedAddresses, list2, true);
		ExpectEquals(std::initializer_list<T>{}, list1);
	}

	template <typename T>
	void TestCopyAssignment(const std::initializer_list<T>& initList)
	{
		ForwardList<T> list1 = initList;
		ForwardList<T> list2;

		list2 = list1;
		ExpectEquals(list1, list2, false);
	}

	template <typename T>
	void TestMoveAssignment(const std::initializer_list<T>& initList)
	{
		ForwardList<T> list1 = initList;
		ForwardList<T> list2;

		std::vector<T*> expectedAddresses;
		expectedAddresses.reserve(initList.size());

		for (auto& element : list1)
			expectedAddresses.push_back(std::addressof(element));

		list2 = std::move(list1);
		ExpectEquals(initList, expectedAddresses, list2, true);
		ExpectEquals(std::initializer_list<T>{}, list1);
	}

	template <typename T>
	void TestSelfCopyAssignment(const std::initializer_list<T>& initList)
	{
		ForwardList<T> list1 = initList;
		
		std::vector<T*> expectedAddresses;
		expectedAddresses.reserve(initList.size());

		for (auto& element : list1)
			expectedAddresses.push_back(std::addressof(element));

		list1 = list1;
		
		ExpectEquals(initList, expectedAddresses, list1, true);
	}

	template <typename T>
	void TestSelfMoveAssignment(const std::initializer_list<T>& initList)
	{
		ForwardList<T> list1 = initList;

		std::vector<T*> expectedAddresses;
		expectedAddresses.reserve(initList.size());

		for (auto& element : list1)
			expectedAddresses.push_back(std::addressof(element));

		list1 = std::move(list1);

		ExpectEquals(initList, expectedAddresses, list1, true);
	}

	template <typename T>
	void TestGetElementFromEmptyList()
	{
		ForwardList<T> list1;

		EXPECT_EQ(0ULL, list1.GetSize());
		EXPECT_THROW(list1.GetFront(), Containers::EmptyContainerException);
	}

	template <typename T>
	void TestPopElementFromEmptyList()
	{
		ForwardList<T> list1;

		EXPECT_EQ(0ULL, list1.GetSize());
		EXPECT_THROW(list1.PopFront(), Containers::EmptyContainerException);
	}

	template <typename T>
	void TestRemoveAfterEndInEmptyList()
	{
		ForwardList<T> list1;

		EXPECT_EQ(0ULL, list1.GetSize());
		EXPECT_FALSE(list1.RemoveAfter(list1.end()));
	}

	template <typename T>
	void TestRemoveAfterBeginInEmptyList()
	{
		ForwardList<T> list1;

		EXPECT_EQ(0ULL, list1.GetSize());
		EXPECT_FALSE(list1.RemoveAfter(list1.begin()));
	}

	template <typename T>
	void TestGetFrontPopFront(const std::initializer_list<T>& init)
	{
		ForwardList<T> list1 = init;
		auto it = init.begin();
		EXPECT_EQ(init.size(), list1.GetSize());
		while (list1.GetSize() > 0)
		{
			auto el = list1.GetFront();
			EXPECT_EQ(*it, el);
			list1.PopFront();
			++it;
		}
	}

	template <typename T>
	void TestRepeatRemoveAfter(const std::initializer_list<T>& init)
	{
		ForwardList<T> list1 = init;
		auto it = init.begin();
		EXPECT_EQ(init.size(), list1.GetSize());
		while (list1.GetSize() > 0)
		{
			auto el = list1.GetFront();
			EXPECT_EQ(*it, el);
			list1.RemoveAfter(list1.BeforeBegin());
			++it;
		}
	}

	template <typename T>
	void TestInsertAfter(const std::initializer_list<T>& init)
	{
		ForwardList<T> list1;
		for (auto& element : init)
			list1.InsertAfter(list1.BeforeBegin(), element);
		for (auto it = rbegin(init); it != rend(init); ++it)
		{
			EXPECT_EQ(*it, list1.GetFront());
			list1.PopFront();
		}
	}

	TEST(ForwardListTest, TestDefaultConstructor) {
		TestDefaultConstructor<int>();
	}

	TEST(ForwardListTest, TestConstructorWithInitializerList) {
		TestConstructorWithInitializerList({1,2,3,4,5,6});
	}

	TEST(ForwardListTest, TestCopyConstructor)
	{
		TestCopyConstructor({1,2,3,4,5});
	}

	TEST(ForwardListTest, TestMoveConstructor)
	{
		TestMoveConstructor({ 1,2,3,4,5 });
	}

	TEST(ForwardListTest, TestCopyAssignment)
	{
		TestCopyAssignment({ 1,2,3,4,5 });
	}

	TEST(ForwardListTest, TestSelfCopyAssignment)
	{
		TestSelfCopyAssignment({ 1,2,3,4,5 });
	}

	TEST(ForwardListTest, TestMoveAssignment)
	{
		TestMoveAssignment({ 1,2,3,4,5 });
	}

	TEST(ForwardListTest, TestSelfMoveAssignment)
	{
		TestSelfMoveAssignment({ 1,2,3,4,5 });
	}

	TEST(ForwardListTest, TestCopyConstructorWithNonTrivialCopyableObjects)
	{
		EXPECT_EQ(0ULL, Countable::GetCounter());
		auto expectedHeapBlocksCount = heapBlocksCounter.load();
		TestCopyConstructor({ Countable{1}, Countable{2}, Countable{3}, Countable{4}, Countable{5}});
		EXPECT_EQ(expectedHeapBlocksCount, heapBlocksCounter.load());
		EXPECT_EQ(0ULL, Countable::GetCounter());
	}

	TEST(ForwardListTest, TestCopyConstructorWithExceptionInCopyConstructor)
	{
		EXPECT_EQ(0ULL, Countable::GetCounter());
		auto expectedHeapBlocksCount = heapBlocksCounter.load();
		try
		{
			TestCopyConstructor({ Countable{1}, Countable{2}, Countable{3}, Countable{4, true, 1}, Countable{5} });
		}
		catch (...)
		{

		}
		EXPECT_EQ(expectedHeapBlocksCount, heapBlocksCounter.load());
		EXPECT_EQ(0ULL, Countable::GetCounter());
	}

	TEST(ForwardListTest, TestCopyConstructorWithExceptionInCopyConstructorOnHeap)
	{
		EXPECT_EQ(0ULL, Countable::GetCounter());
		auto expectedHeapBlocksCount = heapBlocksCounter.load();
		try
		{
			TestCopyConstructorOnHeap({ Countable{1}, Countable{2}, Countable{3}, Countable{4, true, 1}, Countable{5} });
		}
		catch (...)
		{

		}
		EXPECT_EQ(expectedHeapBlocksCount, heapBlocksCounter.load());
		EXPECT_EQ(0ULL, Countable::GetCounter());
	}

	TEST(ForwardListTest, TestGetElementFromEmptyList)
	{
		TestGetElementFromEmptyList<int>();
		TestGetElementFromEmptyList<Countable>();
	}

	TEST(ForwardListTest, TestPopElementFromEmptyList)
	{
		TestPopElementFromEmptyList<int>();
		TestPopElementFromEmptyList<Countable>();
	}

	TEST(ForwardListTest, TestRemoveAfterBeginInEmptyList)
	{
		TestRemoveAfterBeginInEmptyList<int>();
		TestRemoveAfterBeginInEmptyList<Countable>();
	}

	TEST(ForwardListTest, TestRemoveAfterEndInEmptyList)
	{
		TestRemoveAfterEndInEmptyList<int>();
		TestRemoveAfterEndInEmptyList<Countable>();
	}

	TEST(ForwardListTest, TestGetFrontPopFront)
	{
		TestGetFrontPopFront<int>({1,2,3,4,5});
		TestGetFrontPopFront<Countable>({ Countable{ 1 }, Countable{ 2 }, Countable{ 3 }, Countable{ 4 }, Countable{ 5 } });
	}

	TEST(ForwardListTest, TestRepeatRemoveAfter)
	{
		TestRepeatRemoveAfter<int>({ 1,2,3,4,5 });
		TestRepeatRemoveAfter<Countable>({ Countable{ 1 }, Countable{ 2 }, Countable{ 3 }, Countable{ 4 }, Countable{ 5 } });
	}

	TEST(ForwardListTest, TestRepeatInsertAfter)
	{
		TestInsertAfter<int>({ 1,2,3,4,5 });
		TestInsertAfter<Countable>({ Countable{ 1 }, Countable{ 2 }, Countable{ 3 }, Countable{ 4 }, Countable{ 5 } });
	}
}
#endif