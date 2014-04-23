__declspec(dllexport) int printint(int *params, int size) {
	printf("%d", params[0]);
	return 0;
}